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

@Component
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
        Account account = accountService.find(accountId)
                .orElseThrow(()->new IllegalArgumentException("No such account ID:%s%n"
                        .formatted(accountId)));

        String amount = ioHandler.getString("Enter amount to deposit");
        BigDecimal deposit = new BigDecimal(amount);
        if(deposit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("FAIL! Amount %s must be positive\n".formatted(amount));
        }

        int userId = account.userId();
        User user = userService.find(userId)
                .orElseThrow(() -> new IllegalArgumentException(("Data consistency is broken " +
                        "when deposit an account ID:%s for a user ID:%s%n")
                        .formatted(accountId, userId)));
        int accountIndex = user.accounts().indexOf(account);
        Account updated = accountService.deposit(account, deposit);
        user.accounts().set(accountIndex, updated);
        userService.save(user);

        return "Amount %s updated to account ID: %s"
                .formatted(deposit, accountId);
    }
}
