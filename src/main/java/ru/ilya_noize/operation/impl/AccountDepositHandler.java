package ru.ilya_noize.operation.impl;

import org.springframework.beans.factory.annotation.Autowired;
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
public class AccountDepositHandler implements OperationHandler {
    private final Scanner scanner;
    private final AccountService accountService;
    private final UserService userService;

    @Autowired
    public AccountDepositHandler(
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
        return OperationType.ACCOUNT_DEPOSIT;
    }

    @Override
    public void perform() {
        System.out.println("Enter account ID:");
        Long accountId = scanner.nextLong();
        Account account = accountService.find(accountId)
                .orElseThrow(()->new IllegalArgumentException("No such account ID:%s%n"
                        .formatted(accountId)));

        System.out.println("Enter amount to deposit:");
        String amount = scanner.nextLine();
        BigDecimal deposit = new BigDecimal(amount);
        if(deposit.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.printf("FAIL! Amount %s must be positive%n",amount);
        }

        Long userId = account.userId();
        User user = userService.find(userId)
                .orElseThrow(() -> new IllegalArgumentException(("Data consistency is broken " +
                        "when deposit an account ID:%s for a user ID:%s%n")
                        .formatted(accountId, userId)));
        int accountIndex = user.accounts().indexOf(account);
        Account updated = accountService.deposit(account, deposit);
        user.accounts().set(accountIndex, updated);
        userService.save(user);

        System.out.printf("Amount %s updated to account ID: %s", deposit, accountId);
    }
}
