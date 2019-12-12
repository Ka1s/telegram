package com.arteeck.clients;

public interface Client {
    void SendMessage(String text, int chatId);
    void HandleUpdate(String text, int chatId);
}
