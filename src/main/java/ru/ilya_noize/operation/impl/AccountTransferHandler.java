package ru.ilya_noize.operation.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.ilya_noize.model.Account;
import ru.ilya_noize.operation.OperationHandler;
import ru.ilya_noize.operation.OperationType;
import ru.ilya_noize.service.AccountService;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class AccountTransferHandler implements OperationHandler {
    private final Scanner scanner;
    private final AccountService accountService;
    @Value("${account.commission}")
    private BigDecimal commission;

    public AccountTransferHandler(
            Scanner scanner,
            AccountService accountService
    ) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public OperationType getType() {
        return OperationType.ACCOUNT_TRANSFER;
    }

    @Override
    public void perform() {
        System.out.println("Enter source account ID:");
        Long sourceId = scanner.nextLong();
        Account sourceAccount = accountService.find(sourceId);

        System.out.println("Enter target account ID:");
        Long targetId = scanner.nextLong();
        Account targetAccount = accountService.find(targetId);

        Long sourceAccountUserId = sourceAccount.userId();
        Long targetAccountUserId = targetAccount.userId();

        if(sourceAccountUserId.equals(targetAccountUserId)) {
            // TODO: commission = 0
        }

        System.out.println("Enter amount to transfer:");
        String amount = scanner.nextLine();
        BigDecimal transfer = new BigDecimal(amount);

        boolean isOk = accountService.transfer(sourceAccount,targetAccount,transfer);
        String message = isOk ?
                "Amount %s transferred from account ID: %s to account ID: %s" :
                "FAIL! Amount %s can't transferring from account ID: %s to account ID: %s";
        System.out.printf(message, transfer, sourceId, targetId);
    }
}
