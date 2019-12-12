package com.arteeck.commands;

import com.arteeck.clients.AbstractClient;
import com.arteeck.State;

public class HelpCommand extends BaseCommand {

    public HelpCommand(State state)
    {
        stateActions.put(State.Start, this::printHelp);
    }

    private void printHelp(String message, int chatId, AbstractClient client) {
        StringBuilder helpText = new StringBuilder();
        helpText.append("Привет, я новый бот. Я все еще в разработке." +
                " Сейчас ты можешь использовать мои команды: ");
        int counter = 0;
        for (String commandName : client.commands.keySet()) {
            helpText.append(commandName);
            if (counter + 1 != client.commands.size())
                helpText.append(", ");
            counter++;
        }
        client.SendMessage(helpText.toString(), chatId);
    }
}
