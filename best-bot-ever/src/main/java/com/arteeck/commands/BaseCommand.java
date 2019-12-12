package com.arteeck.commands;

import com.arteeck.clients.AbstractClient;
import com.arteeck.State;

import java.util.HashMap;

public abstract class BaseCommand implements Command {
    protected State state = State.Start;
    protected HashMap<State, Command> stateActions = new HashMap<>();

    @Override
    public void run(String message, int chatId, AbstractClient client) {
        stateActions.get(state).run(message, chatId, client);
    }

    public String toStringValue() {
        return String.format("/%s", getClass().getName().toLowerCase().split("command")[0]);
    }

    public State getState() {
        return state;
    }

    protected void sendError(int chatId, AbstractClient client) {
        String answer = "Некорректный ввод, введите корректный номер";
        client.SendMessage(answer, chatId);
    }
}
