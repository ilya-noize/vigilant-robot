package ru.ilya_noize.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class IOHandler {
    private final Scanner scanner;

    @Autowired
    public IOHandler(Scanner scanner) {
        this.scanner = scanner;
    }

    public String getString(String message) {
        String input = "";
        while (input.isBlank()) {
            System.out.printf("│ %s: %n├\uD83E\uDC02 ", message);
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("escape")) {
                throw new IllegalArgumentException("Enter canceled.");
            }
        }
        return input;
    }

    public int getInteger(String message) {
        String string = getString(message);
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            throw new NumberFormatException();
        }
    }
}
