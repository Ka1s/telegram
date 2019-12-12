package com.arteeck.clients;

import com.arteeck.updateHandlers.TelegramUpdateHandler;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class TelegramClient extends AbstractClient {
    public TelegramClient(){
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotApi = new TelegramBotsApi();
        try {
            telegramBotApi.registerBot(new TelegramUpdateHandler());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void SendMessage(String text, int chatId) {

    }
}
