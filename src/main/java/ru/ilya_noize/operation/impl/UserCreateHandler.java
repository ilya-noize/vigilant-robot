package ru.ilya_noize.operation.impl;

import org.springframework.stereotype.Component;
import ru.ilya_noize.model.User;
import ru.ilya_noize.operation.OperationHandler;
import ru.ilya_noize.operation.OperationType;
import ru.ilya_noize.service.UserService;

import java.util.Scanner;

@Component
public class UserCreateHandler implements OperationHandler {
    private final Scanner scanner;
    private final UserService userService;

    public UserCreateHandler(Scanner scanner, UserService userService) {
        this.scanner = scanner;
        this.userService = userService;
    }


    @Override
    public OperationType getType() {
        return OperationType.USER_CREATE;
    }

    @Override
    public void perform() {
        System.out.println("Enter login for new user:");
        String login = scanner.nextLine();
        User user = userService.create(login);
        System.out.printf("User created: %s%n", user);
    }
}
