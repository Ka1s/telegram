package com.arteeck.commands;

import com.arteeck.*;
import com.arteeck.clients.AbstractClient;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public class CopyFromTgCommand extends BaseCommand {

    public CopyFromTgCommand(State state) {
        this.state = state;
        stateActions.put(State.Start, this::askAboutChat);
        stateActions.put(State.WaitingChatTitle, this::handleChatTitle);
        stateActions.put(State.WaitingChatChoice, this::checkChatChoice);
        stateActions.put(State.WaitingUserName, this::saveInfo);
    }

    private void askAboutChat(String message, int chatId, AbstractClient client) {
        client.SendMessage("Напишите имя беседы, или напишите \"отмена\"," +
                " чтобы выйти из этой команды", chatId);
        state = State.WaitingChatTitle;
    }

    private void handleChatTitle(String message, int chatId, AbstractClient client) {
        Bot.addTemporaryInfo(chatId, message);
        List<TgChat> possibleChats = Bot.getPossibleChats(chatId);
        if (possibleChats.size() == 1) {
            askAboutUser(message, chatId, client, possibleChats);
        } else if (possibleChats.size() == 0) {
            client.SendMessage("К сожалению, бот не знает такой беседы в телеграмме", chatId);
            state = State.Start;
        } else
            askAboutChoiceChat(chatId, client, possibleChats);
    }

    private void checkChatChoice(String message, int chatId, AbstractClient client) {
        List<TgChat> possibleChats = Bot.getPossibleChats(chatId);
        try {
            int number = Integer.parseInt(message);
            if (number <= 0 || number > possibleChats.size()) {
                sendError(chatId, client);
            } else
                askAboutUser(message, chatId, client, possibleChats);
        } catch (NumberFormatException exception) {
            sendError(chatId, client);
        }
    }

    private void saveInfo(String userName, int chatId, AbstractClient client) {
        if (userName.equals("Все"))
            userName = userName.toLowerCase();
        if (isForwardingExist(chatId, userName)) {
            client.SendMessage("Вы и так уже копируете все сообщения этого пользователя из этой беседы", chatId);
            state = State.Start;
        } else {
            Bot.addForwarder(new Forwarder(chatId, Bot.getForwardingChat(chatId).getChatId(), userName));
            client.SendMessage("Теперь в эту беседу будут копироваться сообщения из Телеграма", chatId);
            state = State.Start;
        }
    }

    private void askAboutChoiceChat(int chatId, AbstractClient client, List<TgChat> possibleChats) {
        if (!isChatsDifferent(possibleChats)) {
            String answer = "К сожалению, бот нашел несколько бесед с таким названием, но не знает как их различить. " +
                    "Отправьте сообщение в одну из этих бесед, с текстом отличным от текста прошлого сообщения " +
                    "и попробуйте еще раз";
            client.SendMessage(answer, chatId);
            state = State.Start;
        } else {
            StringBuilder answer = new StringBuilder();
            answer.append("Бот нашел несколько бесед с таким названием, по последнему отправленному " +
                    "сообщению в этих беседах определите нужную вам беседу. Отправьте номер выбранной беседы");
            int count = 1;
            for (TgChat chat : possibleChats) {
                answer.append(String.format("\n%d) %s", count, chat.getLastMessageInfo()));
                count++;
            }
            client.SendMessage(answer.toString(), chatId);
            state = State.WaitingChatChoice;
        }
    }

    private void askAboutUser(String message, int chatId, AbstractClient client, List<TgChat> possibleChats) {
        if (state == State.WaitingChatChoice)
            Bot.deleteUnnecessaryInfo(possibleChats.get(Integer.parseInt(message) - 1).getChatId());
        if (isForwardingExist(chatId, "все")) {
            client.SendMessage("Вы и так уже копируете все сообщения из этой беседы", chatId);
            state = State.Start;
        } else {
            String answer = "Напишите имя пользователя, сообщения которого вы хотите копировать из этой беседы " +
                    "или напишите \"все\", если хотите копировать все сообщения из этой беседы" +
                    " или напишите \"отмена\", чтобы выйти из этой команды";
            client.SendMessage(answer, chatId);
            state = State.WaitingUserName;
        }
    }

    public static void forwardMessage(Message message) {
        Chat chat = message.getChat();
        String userName = message.getFrom().getFirstName();
        String text = message.getText();
        List<Forwarder> currentForwarders = Bot.getCurrentForwarders("TgChatId", message.getChatId());
        for (Forwarder forwarder : currentForwarders) {
            if (forwarder.getUserName().equals("все") || forwarder.getUserName().equals(userName))
                Application.vkClient.SendMessage(forwarder.createForwardMessage(chat.getTitle(), userName, text),
                        forwarder.getVkChatId());
        }
    }

    private boolean isChatsDifferent(List<TgChat> chats) {
        int size = chats.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                TgChat firstChat = chats.get(i);
                TgChat secondChat = chats.get(j);
                if (firstChat.getUserName().equals(secondChat.getUserName()) &&
                        firstChat.getLastMessage().equals(secondChat.getLastMessage()))
                    return false;
            }
        }
        return true;
    }

    private boolean isForwardingExist(int chatId, String userName) {
        List<Forwarder> currentForwarders = Bot.getCurrentForwarders("VkChatId", chatId);
        long selectedTgChatId = Bot.getForwardingChat(chatId).getChatId();
        return currentForwarders.contains(new Forwarder(chatId, selectedTgChatId, userName));
    }
}
