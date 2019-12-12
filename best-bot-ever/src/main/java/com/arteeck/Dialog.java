package com.arteeck;

import com.arteeck.clients.AbstractClient;
import com.arteeck.commands.BaseCommand;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Dialog {
    private BaseCommand currentCommand;
    private HashMap<String, BaseCommand> commands = new HashMap<>();
    private int chatId;
    private AbstractClient client;

    public Dialog(int vkChatId, AbstractClient client) {
        chatId = vkChatId;
        this.client = client;
        for (Map.Entry<String, Function<State, BaseCommand>> entry : client.commands.entrySet()) {
            commands.put(entry.getKey(), entry.getValue().apply(State.Start));
        }
    }

    public Dialog(ResultSet info) {
        try {
            chatId = info.getInt("ChatId");
            client = AbstractClient.getClient(info.getString("Client"));
            String stateName = info.getString("State");
            State state = Arrays.stream(State.values())
                    .filter(x -> x.name().equals(stateName))
                    .findAny()
                    .orElse(State.Start);
            for (Map.Entry<String, Function<State, BaseCommand>> entry : client.commands.entrySet()) {
                commands.put(entry.getKey(), entry.getValue().apply(state));
            }
            String command = info.getString("CommandName");
            currentCommand = command.equals("null") ? null : commands.get(command);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public int getChatId() {
        return chatId;
    }

    public String getCommandName() {
        if (currentCommand == null)
            return "null";
        else
            return currentCommand.toStringValue();
    }

    public String getStateName() {
        if (currentCommand == null)
            return State.Start.name();
        else
            return currentCommand.getState().name();
    }

    public String toStringValue() {
        return String.format("%d, '%s', '%s', '%s'",
                chatId, client.toStringValue(), getCommandName(), getStateName());
    }

    public void Run(String message) {
        if (currentCommand == null) {
            CommandParser parser = new CommandParser(message);
            String command = parser.getCommandName();
            message = parser.getCommandText();
            if (commands.containsKey(command))
                currentCommand = commands.get(command);
            else if (command.startsWith("/"))
                client.SendMessage("Я не знаю такую комманду", chatId);
        }
        if (currentCommand != null) {
            if (message.toLowerCase().equals("отмена")) {
                currentCommand = null;
                commands.get("/help").run(message, chatId, client);
            } else {
                currentCommand.run(message, chatId, client);
                if (currentCommand.getState() == State.Start)
                    currentCommand = null;
            }
        }
        if (currentCommand == null)
            Bot.deleteTemporaryInfo(chatId);
    }
}