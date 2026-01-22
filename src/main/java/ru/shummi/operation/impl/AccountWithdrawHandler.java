package ru.shummi.operation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shummi.listener.IOHandler;
import ru.shummi.operation.OperationHandler;
import ru.shummi.operation.OperationType;
import ru.shummi.service.UserAccountService;

import java.math.BigDecimal;

@Component
public class AccountWithdrawHandler implements OperationHandler {
    private final IOHandler ioHandler;
    private final UserAccountService userAccountService;

    @Autowired
    public AccountWithdrawHandler(
            IOHandler ioHandler,
            UserAccountService userAccountService
    ) {
        this.ioHandler = ioHandler;
        this.userAccountService = userAccountService;
    }

    @Override
    public OperationType getType() {
        return OperationType.ACCOUNT_WITHDRAW;
    }

    @Override
    public String perform() {
        long accountId = ioHandler.getLong("Enter account ID to withdraw from");
        String amount = ioHandler.getString("Enter amount to withdraw");
        BigDecimal money = new BigDecimal(amount);
        userAccountService.withdrawAccountById(accountId, money);

        return "Amount of %s has been withdrawn from the account ID: %s"
                .formatted(amount, accountId);
    }
}
