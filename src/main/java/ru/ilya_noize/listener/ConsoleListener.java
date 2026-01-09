package ru.ilya_noize.listener;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ilya_noize.exception.ApplicationException;
import ru.ilya_noize.operation.OperationHandler;
import ru.ilya_noize.operation.OperationType;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
public class ConsoleListener {
    private final IOHandler ioHandler;
    private final Map<OperationType, OperationHandler> handlers;

    @Autowired
    public ConsoleListener(
            IOHandler ioHandler,
            List<OperationHandler> operations
    ) {
        this.ioHandler = ioHandler;
        this.handlers = operations.stream()
                .collect(Collectors.toMap(
                        OperationHandler::getType,
                        operation -> operation
                ));
    }

    @PostConstruct
    public void construct() {
        System.out.println("┌───────────────────────────");
        System.out.println("│  ✅ Initialize listener.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("│  ✅ Shutdown listener.");
        System.out.println("└───────────────────────────");
    }

    public void update() {
        do {
            mainMenu();
            mainHandler();
        } while (!Thread.currentThread().isInterrupted());
    }

    private void mainMenu() {
        System.out.println("├───────────────────────────┐");
        System.out.println("│         MAIN_MENU         │");
        System.out.println("├───────────────────────────┘");
        for (OperationType type : OperationType.values()) {
            System.out.println("├─ " + type);
        }
    }

    private void mainHandler() {
        try {
            commandProcessing();
        } catch (NumberFormatException ignored) {
            System.out.printf("│  ❌ Must be numeric symbols.");
        } catch (ApplicationException |
                 IllegalArgumentException |
                 IllegalStateException |
                 NoSuchElementException e) {
            System.out.printf("│  ❌ %s%n", e.getMessage());
        } catch (Throwable e) {
            System.out.printf("│  ❌ Command fail: %s.%n", e.getMessage());
        }
    }

    private void commandProcessing() {
        String input = ioHandler.getString("Please enter one of operation type");
        OperationType operationType = OperationType.valueOf(input.toUpperCase());
        System.out.printf("│  ✅ %s%n", handlers.get(operationType).perform());
    }
}
