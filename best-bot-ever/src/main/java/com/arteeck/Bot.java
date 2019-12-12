package com.arteeck;

import com.arteeck.clients.AbstractClient;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Bot {
    private static Connection connection;

    private static void addVkDialog(Dialog dialog) throws SQLException {
        reconnectToDataBase();
        connection.createStatement().executeUpdate(String.format("INSERT Dialogs(ChatId, Client, CommandName, State)" +
                "VALUES (%s)", dialog.toStringValue()));
    }

    public static void updateVkDialog(Dialog dialog) throws SQLException {
        reconnectToDataBase();
        connection.createStatement().executeUpdate(
                String.format("UPDATE Dialogs SET CommandName = '%s',State = '%s' WHERE ChatId = %d",
                        dialog.getCommandName(), dialog.getStateName(), dialog.getChatId()));
    }

    public static Dialog getCurrentDialog(int chatId, AbstractClient client) throws SQLException {
        reconnectToDataBase();
        ResultSet result = connection.createStatement().executeQuery("SELECT * FROM Dialogs " +
                "WHERE ChatId = " + chatId);
        Dialog currentDialog = null;
        while (result.next()) {
            currentDialog = new Dialog(result);
        }
        if (currentDialog == null) {
            currentDialog = new Dialog(chatId, client);
            addVkDialog(currentDialog);
        }
        return currentDialog;
    }

    public static void UpdateTgChats(Message message) {
        try {
            reconnectToDataBase();
            if (message.hasText())
                connection.createStatement().executeUpdate(
                        String.format("UPDATE TgChats SET UserName = '%s', LastMessage = '%s' WHERE ChatId = %d",
                                message.getFrom().getFirstName(), message.getText(), message.getChatId()));
            else if (message.getNewChatTitle() != null)
                connection.createStatement().executeUpdate(String.format("UPDATE TgChats SET ChatTitle = '%s' " +
                        "WHERE ChatId = %d", message.getNewChatTitle(), message.getChatId()));
            else if (message.getNewChatMembers().stream()
                    .map(User::getUserName)
                    .collect(Collectors.toList())
                    .contains("CopyMessagesBot")) {
                connection.createStatement().executeUpdate(
                        String.format("INSERT TgChats(ChatId, ChatTitle, UserName, LastMessage) " +
                                "VALUES (%s)", new TgChat(message).toStringValue()));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static TgChat getTgChat(long tgChatId) {
        TgChat tgChat = null;
        try {
            reconnectToDataBase();
            ResultSet result = connection.createStatement().executeQuery(
                    "SELECT * FROM TgChats WHERE TgChats.ChatId = " + tgChatId);
            if (result.next())
                tgChat = new TgChat(result.getLong("ChatId"), result.getString("ChatTitle"),
                        result.getString("UserName"), result.getString("LastMessage"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tgChat;
    }

    public static void addTemporaryInfo(int vkChatId, String tgChatTitle) {
        try {
            reconnectToDataBase();
            ResultSet chats = connection.createStatement().executeQuery(
                    String.format("SELECT ChatId FROM TgChats WHERE TgChats.ChatTitle = '%s'", tgChatTitle));
            while (chats.next())
                connection.createStatement().executeUpdate(
                        String.format("INSERT TemporaryInfo(VkChatId, TgChatTitle, TgChatId) " +
                                "VALUES (%d, '%s', %d)", vkChatId, tgChatTitle, chats.getLong(1)));
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void deleteTemporaryInfo(int vkChatId) {
        try {
            reconnectToDataBase();
            connection.createStatement().executeUpdate("DELETE FROM TemporaryInfo WHERE VkChatId = " + vkChatId);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void deleteUnnecessaryInfo(long tgChatId) {
        try {
            reconnectToDataBase();
            connection.createStatement().executeUpdate("DELETE FROM TemporaryInfo WHERE TgChatId != " + tgChatId);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void addForwarder(Forwarder forwarder) {
        try {
            reconnectToDataBase();
            connection.createStatement().executeUpdate(
                    String.format("INSERT Forwarders(TgChatId, VkChatId, UserName) " +
                            "VALUES (%s)", forwarder.toStringValue()));
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void deleteForwarder(Forwarder forwarder) {
        try {
            reconnectToDataBase();
            connection.createStatement().executeUpdate(String.format("DELETE FROM Forwarders WHERE TgChatId = %d " +
                            "AND VkChatId = %d AND UserName = '%s'",
                    forwarder.getTgChatId(), forwarder.getVkChatId(), forwarder.getUserName()));
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static List<TgChat> getPossibleChats(int vkChatId) {
        List<TgChat> possibleChats = new ArrayList<>();
        try {
            reconnectToDataBase();
            ResultSet result = connection.createStatement().executeQuery(
                    String.format("SELECT DISTINCT tc.* from TgChats as tc, TemporaryInfo as ti " +
                            "WHERE ti.VkChatId = %d AND tc.ChatTitle = ti.TgChatTitle", vkChatId));
            while (result.next())
                possibleChats.add(new TgChat(result.getLong("ChatId"), result.getString("ChatTitle"),
                        result.getString("UserName"), result.getString("LastMessage")));
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return possibleChats;
    }

    public static TgChat getForwardingChat(int vkChatId) {
        TgChat chat = null;
        try {
            reconnectToDataBase();
            ResultSet result = connection.createStatement().executeQuery(
                    String.format("SELECT DISTINCT tc.* from TgChats as tc, TemporaryInfo as ti " +
                            "WHERE ti.VkChatId = %d AND tc.ChatId = ti.TgChatId", vkChatId));
            while (result.next())
                chat = new TgChat(result.getLong("ChatId"), result.getString("ChatTitle"),
                        result.getString("UserName"), result.getString("LastMessage"));
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return chat;
    }

    public static List<Forwarder> getCurrentForwarders(String key, long chatId) {
        List<Forwarder> forwarders = new ArrayList<>();
        try {
            reconnectToDataBase();
            ResultSet result = connection.createStatement().executeQuery(
                    String.format("SELECT * FROM Forwarders WHERE %s = %d", key, chatId));
            while (result.next())
                forwarders.add(new Forwarder(result.getInt("VkChatId"), result.getLong("TgChatId"),
                        result.getString("UserName")));
            return forwarders;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return forwarders;
    }

    public static String getForwardersAsString(int chatId) {
        List<Forwarder> currentForwarders = getCurrentForwarders("VkChatId", chatId);
        StringBuilder answer = new StringBuilder();
        int count = 1;
        for (Forwarder forwarder : currentForwarders) {
            TgChat tgChat = Bot.getTgChat(forwarder.getTgChatId());
            answer.append(String.format("\n%d) Беседа: \"%s\", Пользователь:\"%s\"",
                    count, tgChat.getChatTitle(), forwarder.getUserName()));
            count++;
        }
        return answer.toString();
    }

    public static void connectToDataBase() throws SQLException {
        String url = "jdbc:mysql://remotemysql.com/txw3NvRbKa";
        String username = "txw3NvRbKa";
        String password = "0WUjdfhneD";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException |
                NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        connection = DriverManager.getConnection(url, username, password);
    }

    private static void reconnectToDataBase() throws SQLException {
        while (!connection.isValid(610)) {
            connectToDataBase();
        }
    }
}

