package com.arteeck.commands;

import com.arteeck.clients.AbstractClient;
import com.arteeck.Bot;
import com.arteeck.Forwarder;
import com.arteeck.State;

import java.util.List;

public class RemoveCommand extends BaseCommand {
    public RemoveCommand(State state) {
        this.state = state;
        stateActions.put(State.Start, this::askAboutChat);
        stateActions.put(State.WaitingChatChoice, this::removeForwarder);
    }

    public void askAboutChat(String message, int chatId, AbstractClient client) {
        String currentForwarders = Bot.getForwardersAsString(chatId);
        String answer = "";
        if (currentForwarders.equals("")) {
            answer = "В эту беседу не пересылается ни одна беседа из телеграмма";
        } else {
            answer = "Выберите номер записи о пересылании, которую хотите удалить. " +
                    "Если хотите удалить все, то напишите \"все\"" + currentForwarders;
            state = State.WaitingChatChoice;
        }
        client.SendMessage(answer, chatId);
    }

    private void removeForwarder(String message, int chatId, AbstractClient client) {
        List<Forwarder> currentForwarders = Bot.getCurrentForwarders("VkChatId", chatId);
        if (message.toLowerCase().equals("все")) {
            for (Forwarder forwarder : currentForwarders) {
                Bot.deleteForwarder(forwarder);
            }
        } else {
            try {
                int number = Integer.parseInt(message);
                if (number <= 0 || number > currentForwarders.size()) {
                    sendError(chatId, client);
                } else
                    Bot.deleteForwarder(currentForwarders.get(number - 1));
            } catch (NumberFormatException exception) {
                sendError(chatId, client);
            }
        }
        client.SendMessage("Удаление произошло успешно", chatId);
        state = State.Start;
    }
}
