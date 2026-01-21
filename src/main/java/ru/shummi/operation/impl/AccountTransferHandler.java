package ru.shummi.operation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shummi.listener.IOHandler;
import ru.shummi.operation.OperationHandler;
import ru.shummi.operation.OperationType;
import ru.shummi.service.UserAccountService;

import java.math.BigDecimal;

/**
 * Перевод между счетами пользователя(ей)
 */
@Component
public class AccountTransferHandler implements OperationHandler {
    private final IOHandler ioHandler;
    private final UserAccountService userAccountService;

    @Autowired
    public AccountTransferHandler(
            IOHandler ioHandler,
            UserAccountService userAccountService
    ) {
        this.ioHandler = ioHandler;
        this.userAccountService = userAccountService;
    }

    @Override
    public OperationType getType() {
        return OperationType.ACCOUNT_TRANSFER;
    }

    @Override
    public String perform() {
        long sourceAccountId = ioHandler.getLong("Enter source account ID");
        long targetAccountId = ioHandler.getLong("Enter target account ID");
        String amount = ioHandler.getString("Enter amount to transfer");
        BigDecimal transfer = new BigDecimal(amount);
        userAccountService.transferAccountFromIdToId(sourceAccountId, targetAccountId, transfer);

        return "Amount %s transferred from account ID: %s to account ID: %s"
                .formatted(amount, sourceAccountId, targetAccountId);
    }
}
