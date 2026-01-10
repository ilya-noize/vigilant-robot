package ru.ilya_noize.operation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ilya_noize.exception.ApplicationException;
import ru.ilya_noize.listener.IOHandler;
import ru.ilya_noize.model.Account;
import ru.ilya_noize.model.User;
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
        if (userService.isEmpty()) {
            throw new ApplicationException("The list of users is empty. " +
                    "Creating an account is not possible.");
        }

        int userId = ioHandler.getInteger("Enter the user id for which to create an account");
        int accountId = accountCreate(userId);

        return "New account created with ID: %s for user ID: %s"
                .formatted(accountId, userId);
    }

    private int accountCreate(int userId) {
        Account account = accountService.create(userId);
        User user = userService.get(userId);
        user.addAccount(account);
        return account.id();
    }
}
