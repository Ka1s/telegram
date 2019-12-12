package com.arteeck.commands;

import com.arteeck.clients.AbstractClient;

public interface Command {
    void run(String message, int chatId, AbstractClient client);
}
