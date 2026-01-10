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
        String amount = ioHandler.getString("Enter amount to withdraw");
        AccountWithdraw(accountId, amount);

        return "Amount %s deposited to account ID: %s"
                .formatted(amount, accountId);
    }

    public void AccountWithdraw(int accountId, String amount) {
        Account account = accountService.get(accountId);
        User user = userService.get(account.userId());

        BigDecimal money = new BigDecimal(amount);
        if(money.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("FAIL! Amount %s must be positive\n".formatted(amount));
        }
        account.withdrawMoney(money);
        user.updateAccount(account);
    }
}
