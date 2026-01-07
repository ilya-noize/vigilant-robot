package ru.ilya_noize.listener;

import org.springframework.stereotype.Component;
import ru.ilya_noize.operation.OperationHandler;
import ru.ilya_noize.operation.OperationType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class ConsoleListener {
    private final Scanner scanner;
    private final Map<OperationType, OperationHandler> handlers;

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
        System.out.println("Please enter one of operation type:");
        do {
            for (OperationType type : OperationType.values()) {
                System.out.println(type);
            }
            Optional<OperationType> operationType = getOperationType();
            if (operationType.isEmpty()) {
                return;
            }
            operationHandler(operationType.get());
        } while (!Thread.currentThread().isInterrupted());
    }

    public void construct() {
        System.out.println("Initialize listener.");
    }

    public void destroy() {
        System.out.println("Shutdown listener.");
    }

    private Optional<OperationType> getOperationType() {
        while (!Thread.currentThread().isInterrupted()) {
            String input = scanner.nextLine();
            try {
                return Optional.of(OperationType.valueOf(input));
            } catch (IllegalArgumentException e) {
                System.out.println("No such command found");
            }
        }
        return Optional.empty();
    }

    private void operationHandler(OperationType operationType) {
        try {
            handlers.get(operationType).perform();
        } catch (Exception e) {
            System.out.printf("Operation: %s is fail: %s.%n",
                    operationType,
                    e.getMessage());
        }
    }
}
