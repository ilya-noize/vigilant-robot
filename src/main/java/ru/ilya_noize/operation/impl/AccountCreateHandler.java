package ru.ilya_noize.operation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ilya_noize.model.Account;
import ru.ilya_noize.model.User;
import ru.ilya_noize.operation.OperationHandler;
import ru.ilya_noize.operation.OperationType;
import ru.ilya_noize.service.AccountService;
import ru.ilya_noize.service.UserService;

import java.util.Optional;
import java.util.Scanner;

@Component
public class AccountCreateHandler implements OperationHandler {
    private final Scanner scanner;
    private final AccountService accountService;
    private final UserService userService;

    @Autowired
    public AccountCreateHandler(Scanner scanner, AccountService accountService, UserService userService) {
        this.scanner = scanner;
        this.accountService = accountService;
        this.userService = userService;
    }

    @Override
    public OperationType getType() {
        return OperationType.ACCOUNT_CREATE;
    }

    @Override
    public void perform() {
        System.out.println("Enter the user id for which to create an account:");
        Long userId = scanner.nextLong();
        Optional<User> optionalUser = userService.find(userId);
        if (optionalUser.isEmpty()) {
            System.out.printf("No such user with ID:%s%n", userId);
            return;
        }
        User user = optionalUser.get();
        Account account = accountService.create(user);
        System.out.printf("New account created with ID: %s for user: %s%n",
                account.id(),
                user);
    }
}
