package com.arteeck.updateHandlers;

import com.arteeck.Bot;
import com.arteeck.commands.CopyFromTgCommand;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramUpdateHandler extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        Bot.UpdateTgChats(message);
        CopyFromTgCommand.forwardMessage(message);

    }

    @Override
    public String getBotUsername() {
        return "***";
    }

    @Override
    public String getBotToken() {
        return "***";
    }
}