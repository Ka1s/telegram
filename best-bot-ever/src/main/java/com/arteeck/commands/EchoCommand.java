package com.arteeck.commands;

import com.arteeck.clients.AbstractClient;
import com.arteeck.State;

public class EchoCommand extends BaseCommand {

    public EchoCommand(State state) {
        stateActions.put(State.Start, this::echo);
    }
    private void echo(String message, int chatId, AbstractClient client) {
        if (!message.equals(" "))
            client.SendMessage(message, chatId);
        else
            client.SendMessage("Эй, что за путоста?", chatId);
    }


}
