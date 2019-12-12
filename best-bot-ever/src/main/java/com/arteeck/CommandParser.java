package com.arteeck;

import java.util.Arrays;

public class CommandParser {
    private String[] words;

    public CommandParser(String message) {
        words = message.split(" ");
    }

    public String getCommandName() {
        return words[0].toLowerCase();
    }

    public String getCommandText() {
        if (words.length > 1)
            return String.join(" ", Arrays.copyOfRange(words, 1, words.length));
        return " ";
    }
}
