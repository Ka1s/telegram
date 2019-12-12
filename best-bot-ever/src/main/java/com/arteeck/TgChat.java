package com.arteeck;

import org.telegram.telegrambots.meta.api.objects.Message;

public class TgChat {
    private long chatId;
    private String chatTitle;
    private String userName;
    private String lastMessage;

    public TgChat(Message message) {
        lastMessage = message.getText();
        userName = message.getFrom().getFirstName();
        chatTitle = message.getChat().getTitle();
        chatId = message.getChatId();
    }

    public TgChat(long chatId, String chatTitle, String userName, String lastMessage) {
        this.chatTitle = chatTitle;
        this.chatId = chatId;
        this.userName = userName;
        this.lastMessage = lastMessage;
    }


    public String toStringValue() {
        return String.format("%d, '%s', '%s', '%s'",
                chatId, chatTitle, userName, lastMessage);
    }

    public long getChatId() {
        return chatId;
    }

    public String getChatTitle() {
        return chatTitle;
    }

    public String getUserName() {
        return userName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastMessageInfo() {
        return String.format("Последнее сообщение: \"%s\" отправлено пользователем \"%s\"", lastMessage, userName);
    }
}
