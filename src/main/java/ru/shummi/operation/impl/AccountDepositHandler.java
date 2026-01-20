package ru.shummi.operation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shummi.listener.IOHandler;
import ru.shummi.operation.OperationHandler;
import ru.shummi.operation.OperationType;
import ru.shummi.service.UserAccountService;

import java.math.BigDecimal;

@Component("deposit")
public class AccountDepositHandler implements OperationHandler {
    private final IOHandler ioHandler;
    private final UserAccountService userAccountService;

    @Autowired
    public AccountDepositHandler(
            IOHandler ioHandler,
            UserAccountService userAccountService
    ) {
        this.ioHandler = ioHandler;
        this.userAccountService = userAccountService;
    }

    @Override
    public OperationType getType() {
        return OperationType.ACCOUNT_DEPOSIT;
    }

    @Override
    public String perform() {
        int accountId = ioHandler.getInteger("Enter account ID");
        String amount = ioHandler.getString("Enter amount to deposit");
        BigDecimal deposit = new BigDecimal(amount);
        userAccountService.depositAccountById(accountId, deposit);

        return "Amount %s updated to account ID: %s"
                .formatted(amount, accountId);
    }
}
