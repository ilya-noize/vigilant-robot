package ru.shummi.listener;

import org.springframework.stereotype.Component;
import ru.shummi.exception.ApplicationException;

import java.util.Scanner;

@Component
public class IOHandler {
    private final Scanner scanner;

    public IOHandler() {
        this.scanner = new Scanner(System.in);
    }

    public String getString(String message) {
        String input = "";
        while (input.isBlank()) {
            System.out.printf("│ %s: %n├\uD83E\uDC02 ", message);
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("escape")) {
                throw new ApplicationException("enter canceled.");
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

    public long getLong(String message) {
        String string = getString(message);
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            throw new NumberFormatException();
        }
    }
}
