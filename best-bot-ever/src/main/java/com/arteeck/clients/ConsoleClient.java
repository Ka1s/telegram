package com.arteeck.clients;

import com.arteeck.commands.AuthorsCommand;
import com.arteeck.commands.EchoCommand;
import com.arteeck.commands.EchoVkCommand;
import com.arteeck.commands.HelpCommand;

public class ConsoleClient extends AbstractClient {
    public ConsoleClient(){
        commands.put("/echo", EchoCommand::new);
        commands.put("/help", HelpCommand::new);
        commands.put("/echovk", EchoVkCommand::new);
        commands.put("/authors", AuthorsCommand::new);
    }
    @Override
    public void SendMessage(String text, int chatId) {
        System.out.println(text);
    }
}
