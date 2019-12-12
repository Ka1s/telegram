package com.arteeck.updateHandlers;

import com.arteeck.Application;

import java.util.Scanner;

public class ConsoleUpdateHandler {
    public static void Run() {
        Scanner input = new Scanner(System.in);
        while (true) {
            String text = input.nextLine();
            Application.consoleClient.HandleUpdate(text,0);
        }
    }
}
