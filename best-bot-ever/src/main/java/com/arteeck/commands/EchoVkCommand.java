package com.arteeck.commands;

import com.arteeck.clients.AbstractClient;
import com.arteeck.Application;
import com.arteeck.State;

public class EchoVkCommand extends BaseCommand {
    public EchoVkCommand(State state)
    {
        stateActions.put(State.Start, this::echoVk);
    }

    private void echoVk(String message, int chatId, AbstractClient client) {
        if (!message.equals(" "))
            Application.vkClient.SendMessage(message, 2000000001);
        else
            Application.vkClient.SendMessage("Эй, что за путоста?", 2000000001);

    }
}
