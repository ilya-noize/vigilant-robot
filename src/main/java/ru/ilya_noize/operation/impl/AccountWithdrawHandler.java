package ru.ilya_noize.operation.impl;

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
public class AccountWithdrawHandler implements OperationHandler {
    private final IOHandler ioHandler;
    private final AccountService accountService;
    private final UserService userService;

    public AccountWithdrawHandler(
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
        return OperationType.ACCOUNT_WITHDRAW;
    }

    @Override
    public String perform() {
        int accountId = ioHandler.getInteger("Enter account ID to withdraw from");
        Account account = accountService.find(accountId)
                .orElseThrow(() -> new IllegalArgumentException("No such account ID:%s"
                        .formatted(accountId)
                ));

        String amount = ioHandler.getString("Enter amount to withdraw");
        BigDecimal withdraw = new BigDecimal(amount);
        accountService.save(account);
        int userId = account.userId();
        User user = userService.find(userId)
                .orElseThrow(() -> new IllegalStateException("Data consistency is broken " +
                        "when closing an account ID:%s for a user ID:%s"
                                .formatted(accountId, userId)
                ));
        int indexOfAccount = user.accounts().indexOf(account);
        accountService.save(account.withdrawMoney(withdraw));
        user.accounts().set(indexOfAccount, account);
        userService.save(user);

        return "Amount %s deposited to account ID: %s"
                .formatted(withdraw, accountId);
    }
}
