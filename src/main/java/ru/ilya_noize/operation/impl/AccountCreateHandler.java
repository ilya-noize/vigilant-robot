package ru.ilya_noize.operation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ilya_noize.listener.IOHandler;
import ru.ilya_noize.model.Account;
import ru.ilya_noize.operation.OperationHandler;
import ru.ilya_noize.operation.OperationType;
import ru.ilya_noize.service.AccountService;
import ru.ilya_noize.service.UserService;

@Component
public class AccountCreateHandler implements OperationHandler {
    private final IOHandler ioHandler;
    private final AccountService accountService;
    private final UserService userService;

    @Autowired
    public AccountCreateHandler(IOHandler ioHandler, AccountService accountService, UserService userService) {
        this.ioHandler = ioHandler;
        this.accountService = accountService;
        this.userService = userService;
    }

    @Override
    public OperationType getType() {
        return OperationType.ACCOUNT_CREATE;
    }

    @Override
    public String perform() {
        if (userService.usersIsEmpty()) {
            throw new IllegalStateException("The list of users is empty. " +
                    "Creating an account is not possible.");
        }
        int userId = ioHandler.getInteger("Enter the user id for which to create an account");
        if (userService.notExists(userId)) {
            throw new IllegalArgumentException("No such user with ID:%s"
                    .formatted(userId));
        }
        Account account = accountService.create(userId);
        userService.addAccount(account);
        return "New account created with ID: %s for user ID: %s"
                .formatted(account.id(), userId);
    }
}
