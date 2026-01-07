package ru.ilya_noize.operation.impl;

import org.springframework.stereotype.Component;
import ru.ilya_noize.model.Account;
import ru.ilya_noize.operation.OperationHandler;
import ru.ilya_noize.operation.OperationType;
import ru.ilya_noize.service.AccountService;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class AccountDepositHandler implements OperationHandler {
    private final Scanner scanner;
    private final AccountService accountService;

    public AccountDepositHandler(
            Scanner scanner,
            AccountService accountService
    ) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public OperationType getType() {
        return OperationType.ACCOUNT_DEPOSIT;
    }

    @Override
    public void perform() {
        System.out.println("Enter account ID:");
        Long accountId = scanner.nextLong();
        Account account = accountService.find(accountId);

        System.out.println("Enter amount to deposit:");
        String amount = scanner.nextLine();
        BigDecimal deposit = new BigDecimal(amount);
        if(deposit.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.printf("FAIL! Amount %s must be positive%n",amount);
        }

        boolean isOk = accountService.deposit(account, deposit);
        String message = isOk ?
                "Amount %s deposited to account ID: %s" :
                "FAIL! Amount %s can't depositing to account ID: %s";
        System.out.printf(message, deposit, accountId);
    }
}
