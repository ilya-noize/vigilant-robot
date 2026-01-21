package ru.shummi.operation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shummi.listener.IOHandler;
import ru.shummi.operation.OperationHandler;
import ru.shummi.operation.OperationType;
import ru.shummi.service.UserAccountService;

@Component
public class AccountCreateHandler implements OperationHandler {
    private final IOHandler ioHandler;
    private final UserAccountService userAccountService;

    @Autowired
    public AccountCreateHandler(
            IOHandler ioHandler,
            UserAccountService userAccountService
    ) {
        this.ioHandler = ioHandler;
        this.userAccountService = userAccountService;
    }

    @Override
    public OperationType getType() {
        return OperationType.ACCOUNT_CREATE;
    }

    @Override
    public String perform() {
        long userId = ioHandler.getLong("Enter the user id for which to create an account");
        long accountId = userAccountService.addAccountToUserById(userId).id();

        return "New account created with ID: %s for user ID: %s"
                .formatted(accountId, userId);
    }
}
