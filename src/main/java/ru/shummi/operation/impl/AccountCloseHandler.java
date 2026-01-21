package ru.shummi.operation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shummi.listener.IOHandler;
import ru.shummi.operation.OperationHandler;
import ru.shummi.operation.OperationType;
import ru.shummi.service.UserAccountService;

@Component
public class AccountCloseHandler implements OperationHandler {
    private final IOHandler ioHandler;
    private final UserAccountService userAccountService;

    @Autowired
    public AccountCloseHandler(
            IOHandler ioHandler,
            UserAccountService userAccountService
    ) {
        this.ioHandler = ioHandler;
        this.userAccountService = userAccountService;
    }

    @Override
    public OperationType getType() {
        return OperationType.ACCOUNT_CLOSE;
    }

    @Override
    public String perform() {
        long accountId = ioHandler.getLong("Enter account ID to close");
        userAccountService.closeAccountById(accountId);

        return "Account with ID %s has been closed.".formatted(accountId);
    }
}
