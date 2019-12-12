package com.arteeck.commands;

import com.arteeck.clients.AbstractClient;
import com.arteeck.State;

public class AuthorsCommand extends BaseCommand {
    public AuthorsCommand(State state) {
        stateActions.put(State.Start, this::printAuthors);
    }

    private void printAuthors(String message, int chatId, AbstractClient client) {
        String authorsText = "Авторы бота: Гордон Артемий и Набокин Иван";
        client.SendMessage(authorsText, chatId);
    }
}
