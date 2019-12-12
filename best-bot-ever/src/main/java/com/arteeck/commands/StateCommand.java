package com.arteeck.commands;

import com.arteeck.clients.AbstractClient;
import com.arteeck.Bot;
import com.arteeck.State;

public class StateCommand extends BaseCommand {
    public StateCommand(State state) {
        stateActions.put(State.Start, this::printState);
    }

    private void printState(String Message, int chatId, AbstractClient client) {
        String currentForwarders = Bot.getForwardersAsString(chatId);
        String answer = "";
        if (currentForwarders.equals("")) {
            answer = "В эту беседу не пересылается ни одна беседа из телеграмма";
        } else {
            answer = "Беседы из которых пересылаются сообщения в эту беседу" + currentForwarders;
        }
        client.SendMessage(answer, chatId);
    }
}
