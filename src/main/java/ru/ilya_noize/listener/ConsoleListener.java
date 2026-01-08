package ru.ilya_noize.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ilya_noize.operation.OperationHandler;
import ru.ilya_noize.operation.OperationType;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class ConsoleListener {
    private final Scanner scanner;
    private final Map<OperationType, OperationHandler> handlers;

    @Autowired
    public ConsoleListener(Scanner scanner,
                           List<OperationHandler> operations) {
        this.scanner = scanner;
        this.handlers = operations.stream()
                .collect(Collectors.toMap(
                        OperationHandler::getType,
                        operation -> operation
                ));
    }

    public void update() {
        do {
            for (OperationType type : OperationType.values()) {
                System.out.println(type);
            }
            System.out.println("Please enter one of operation type:");
            String input = scanner.nextLine();
            try {
                OperationType operationType = OperationType.valueOf(input);
                handlers.get(operationType).perform();
            } catch (IllegalArgumentException e) {
                System.out.println("No such command found");
            } catch (Exception e) {
                System.out.printf("Operation: %s is fail: %s.%n", input, e.getMessage());
            }
        } while (!Thread.currentThread().isInterrupted());

    }

    public void construct() {
        System.out.println("Initialize listener.");
    }

    public void destroy() {
        System.out.println("Shutdown listener.");
    }
}
