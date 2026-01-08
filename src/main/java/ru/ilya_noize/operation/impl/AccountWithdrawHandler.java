package ru.ilya_noize.operation.impl;

import org.springframework.stereotype.Component;
import ru.ilya_noize.model.Account;
import ru.ilya_noize.model.User;
import ru.ilya_noize.operation.OperationHandler;
import ru.ilya_noize.operation.OperationType;
import ru.ilya_noize.service.AccountService;
import ru.ilya_noize.service.UserService;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class AccountWithdrawHandler implements OperationHandler {
    private final Scanner scanner;
    private final AccountService accountService;
    private final UserService userService;

    public AccountWithdrawHandler(
            Scanner scanner,
            AccountService accountService,
            UserService userService
    ) {
        this.scanner = scanner;
        this.accountService = accountService;
        this.userService = userService;
    }

    @Override
    public OperationType getType() {
        return OperationType.ACCOUNT_WITHDRAW;
    }

    @Override
    public void perform() {
        System.out.println("Enter account ID to withdraw from:");
        Long accountId = scanner.nextLong();
        Account account = accountService.find(accountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No such account ID:%s".formatted(accountId)
                ));

        System.out.println("Enter amount to withdraw:");
        String amount = scanner.nextLine();
        BigDecimal withdraw = new BigDecimal(amount);
        accountService.save(account);
        long userId = account.userId();
        User user = userService.find(userId)
                .orElseThrow(() -> new IllegalStateException("Data consistency is broken " +
                        "when closing an account ID:%s for a user ID:%s%n"
                                .formatted(accountId, userId)
                ));
        int indexOfAccount = user.accounts().indexOf(account);
        accountService.save(account.withdrawMoney(withdraw));
        user.accounts().set(indexOfAccount, account);
        userService.save(user);

        System.out.printf("Amount %s deposited to account ID: %s", withdraw, accountId);
    }
}
