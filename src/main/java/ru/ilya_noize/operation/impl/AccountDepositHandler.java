package ru.ilya_noize.operation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ilya_noize.listener.IOHandler;
import ru.ilya_noize.model.Account;
import ru.ilya_noize.model.User;
import ru.ilya_noize.operation.OperationHandler;
import ru.ilya_noize.operation.OperationType;
import ru.ilya_noize.service.AccountService;
import ru.ilya_noize.service.UserService;

import java.math.BigDecimal;

@Component("deposit")
public class AccountDepositHandler implements OperationHandler {
    private final IOHandler ioHandler;
    private final AccountService accountService;
    private final UserService userService;

    @Autowired
    public AccountDepositHandler(
            IOHandler ioHandler,
            AccountService accountService,
            UserService userService
    ) {
        this.ioHandler = ioHandler;
        this.accountService = accountService;
        this.userService = userService;
    }

    @Override
    public OperationType getType() {
        return OperationType.ACCOUNT_DEPOSIT;
    }

    @Override
    public String perform() {
        int accountId = ioHandler.getInteger("Enter account ID");
        String amount = ioHandler.getString("Enter amount to deposit");

        accountDeposit(accountId, amount);
        return "Amount %s updated to account ID: %s"
                .formatted(amount, accountId);
    }

    public void accountDeposit(int accountId, String amount) {
        BigDecimal deposit = new BigDecimal(amount);
        if (deposit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("FAIL! Amount %s must be positive"
                    .formatted(amount));
        }
        Account account = accountService.get(accountId);
        User user = userService.get(account.userId());
        account.depositMoney(deposit);
        user.updateAccount(account);
    }
}
