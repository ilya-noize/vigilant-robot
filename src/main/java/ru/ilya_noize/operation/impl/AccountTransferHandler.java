package ru.ilya_noize.operation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class AccountTransferHandler implements OperationHandler {
    private final Scanner scanner;
    private BigDecimal commission;
    private final AccountService accountService;
    private final UserService userService;

    @Autowired
    public AccountTransferHandler(
            Scanner scanner,
            @Value("${account.commission}")
            String commission,
            AccountService accountService,
            UserService userService
    ) {
        this.scanner = scanner;
        if (commission == null) {
            commission = "0";
        }
        this.commission = new BigDecimal(commission);
        this.accountService = accountService;
        this.userService = userService;
    }

    @Override
    public OperationType getType() {
        return OperationType.ACCOUNT_TRANSFER;
    }

    @Override
    public void perform() {
        System.out.println("Enter source account ID:");
        long sourceAccountId = scanner.nextLong();


        System.out.println("Enter target account ID:");
        long targetAccountId = scanner.nextLong();

        System.out.println("Enter amount to transfer:");
        String amount = scanner.nextLine();
        BigDecimal transfer = new BigDecimal(amount);

        moneyTransfer(sourceAccountId, targetAccountId, transfer);

        System.out.printf("Amount %s transferred from account ID: %s to account ID: %s",
                transfer, sourceAccountId, targetAccountId);
    }

    private void moneyTransfer(
            long sourceAccountId,
            long targetAccountId,
            BigDecimal transfer
    ) {
        String source = AccountType.SOURCE.name();
        Account sourceAccount = getAccount(sourceAccountId, source);
        User sourceUser = getUser(sourceAccountId, sourceAccount.userId(), source);


        String target = AccountType.TARGET.name();
        Account targetAccount = getAccount(targetAccountId, target);
        User targetUser = getUser(targetAccountId, targetAccount.userId(), target);

        if (sourceUser.equals(targetUser)) {
            commission = BigDecimal.ZERO;
        }

        synchronized (userService) {
            BigDecimal multiplied = transfer.multiply(BigDecimal.ONE.add(commission));
            boolean approve = transferring(sourceUser, sourceAccount, multiplied, true);
            boolean complete = transferring(targetUser, targetAccount, transfer, approve);
            String message = complete ? "Amount %s transferred from account ID:%s to account ID:%s.%n" :
                    "ERROR: Amount %s reject transferring from account ID:%s to account ID:%s.%n";
            System.out.printf(message, transfer, sourceAccountId, targetAccountId);
        }
    }

    private boolean transferring(
            User user,
            Account account,
            BigDecimal amount,
            boolean agree
    ) {
        try {
            if (agree) {
                int indexSourceAccount = user.accounts().indexOf(account);
                account = accountService.withdraw(account, amount);
                user.accounts().set(indexSourceAccount, account);
                userService.save(user);
                return true;
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return false;
        }
    }

    private Account getAccount(
            long accountId,
            String accountType
    ) {
        return accountService.find(accountId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No such %s account ID:%s%n".formatted(accountType.toLowerCase(), accountId)
                ));
    }

    private User getUser(
            long sourceAccountId,
            long sourceAccountUserId,
            String accountType
    ) {
        return userService.find(sourceAccountUserId)
                .orElseThrow(() -> new IllegalArgumentException(("Data consistency is broken " +
                        "when transfer an %s account ID:%s for a user ID:%s%n")
                        .formatted(accountType.toLowerCase(), sourceAccountId, sourceAccountUserId)
                ));
    }

    enum AccountType {
        SOURCE,
        TARGET
    }
}
