package com.arteeck.clients;

import com.arteeck.Application;
import com.arteeck.Bot;
import com.arteeck.Dialog;
import com.arteeck.State;
import com.arteeck.commands.BaseCommand;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.function.Function;

public abstract class AbstractClient implements Client {
    public HashMap<String, Function<State, BaseCommand>> commands = new HashMap<>();

    public abstract void SendMessage(String text, int chatId);

    public String toStringValue() {
        return getClass().getName().toLowerCase();
    }

    public void HandleUpdate(String text, int chatId) {
        try {
            Dialog currentDialog = Bot.getCurrentDialog(chatId, this);
            currentDialog.Run(text);
            Bot.updateVkDialog(currentDialog);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static AbstractClient getClient(String name) {
        if (name.equals("vkclient"))
            return Application.vkClient;
        if (name.equals("consoleclient"))
            return Application.consoleClient;
        return Application.tgClient;
    }
}
