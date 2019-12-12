package com.arteeck;

import com.arteeck.clients.ConsoleClient;
import com.arteeck.clients.TelegramClient;
import com.arteeck.clients.VKClient;
import com.arteeck.updateHandlers.VKUpdateHandler;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

import java.sql.SQLException;

public class Application {
    public static VKClient vkClient = new VKClient();
    public static TelegramClient tgClient = new TelegramClient();
    public static ConsoleClient consoleClient = new ConsoleClient();

    public static void main(String[] args) {
        try {
            Bot.connectToDataBase();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        VKUpdateHandler vkUpdateHandler = new VKUpdateHandler(vkClient.getVk(), vkClient.getGroup());
        try {
            vkUpdateHandler.run();
        } catch (ClientException | ApiException e) {
            e.printStackTrace();
        }

    }
}

