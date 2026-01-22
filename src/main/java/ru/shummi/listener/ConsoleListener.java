package ru.shummi.listener;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shummi.exception.ApplicationException;
import ru.shummi.operation.OperationHandler;
import ru.shummi.operation.OperationType;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
public class ConsoleListener {
    private final int width = 28;

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
        System.out.println("┌" + "─".repeat(width - 1));
        System.out.println("│  ✅ Initialize listener.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("│  ✅ Shutdown listener.");
        System.out.println("└" + "─".repeat(width - 1));
    }

    public void update() {
        do {
            mainMenu();
            mainHandler();
        } while (!Thread.currentThread().isInterrupted());
    }

    private void mainMenu() {
        System.out.println("├" + "─".repeat(width - 2) + "┐");
        System.out.println("│        MAIN MENU         │");
        for (OperationType type : OperationType.values()) {
            // "├─ %-Ns│%n" -> N = width - 6
            System.out.printf("├─ %-24s│%n", type);
        }
        System.out.println("├" + "─".repeat(width - 2) + "┘");
    }

    private void mainHandler() {
        try {
            commandProcessing();
        } catch (NumberFormatException ignored) {
            System.out.println("│  ❌ Must be numeric symbols.");
        } catch (ApplicationException e) {
            System.out.printf("│  ❌ Application: %s%n", e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.printf("│  ❌ Illegal Argument: %s%n", e.getMessage());
        } catch (IllegalStateException e) {
            System.out.printf("│  ❌ Illegal State: %s%n", e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.printf("│  ❌ No Such: %s%n", e.getMessage());
        } catch (Exception e) {
            System.out.printf("│  ❌ Exception: %s.%n", e.getMessage());
        } catch (Throwable e) {
            System.out.printf("│  ❌ Throwable: %s.%n", e.getMessage());
        }
    }

    private void commandProcessing() {
        String input = ioHandler.getString("Please enter one of operation type");
        OperationType operationType = OperationType.valueOf(input.toUpperCase());
        System.out.printf("│  ✅ %s%n", handlers.get(operationType).perform());
    }
}
