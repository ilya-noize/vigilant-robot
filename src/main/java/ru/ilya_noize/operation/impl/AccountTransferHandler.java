package ru.ilya_noize.operation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.ilya_noize.exception.ApplicationException;
import ru.ilya_noize.listener.IOHandler;
import ru.ilya_noize.model.Account;
import ru.ilya_noize.model.User;
import ru.ilya_noize.operation.OperationHandler;
import ru.ilya_noize.operation.OperationType;
import ru.ilya_noize.service.AccountService;
import ru.ilya_noize.service.UserService;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static java.math.BigDecimal.ZERO;
import static ru.ilya_noize.operation.impl.AccountTransferHandler.AccountType.SOURCE;
import static ru.ilya_noize.operation.impl.AccountTransferHandler.AccountType.TARGET;

/**
 * Перевод между счетами пользователя(ей)
 */
@Component
public class AccountTransferHandler implements OperationHandler {
    private final IOHandler ioHandler;
    private BigDecimal commission;
    private final AccountService accountService;
    private final UserService userService;

    @Autowired
    public AccountTransferHandler(
            IOHandler ioHandler,
            @Value("${account.commission}")
            String commission,
            AccountService accountService,
            UserService userService
    ) {
        this.ioHandler = ioHandler;
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
    public String perform() {
        int sourceAccountId = ioHandler.getInteger("Enter source account ID");
        int targetAccountId = ioHandler.getInteger("Enter target account ID");
        String amount = ioHandler.getString("Enter amount to transfer");
        BigDecimal transfer = new BigDecimal(amount);
        if (transfer.compareTo(ZERO) < 1){
            throw new ApplicationException("Amount must be positive");
        }
        moneyTransfer(sourceAccountId, targetAccountId, transfer);

        return "Amount %s transferred from account ID: %s to account ID: %s"
                .formatted(transfer, sourceAccountId, targetAccountId);
    }

    /**
     * Расчёт комиссии за перевод и синхронизация финансовых операций
     *
     * @param sourceAccountId номер счёта списания
     * @param targetAccountId номер счёта внесения
     * @param transfer        сумма перевода
     */
    private void moneyTransfer(int sourceAccountId,
                               int targetAccountId,
                               BigDecimal transfer
    ) {
        Account sourceAccount = getAccount(sourceAccountId, SOURCE);
        User sourceUser = getUser(sourceAccount.userId(), SOURCE);

        Account targetAccount = getAccount(targetAccountId, TARGET);
        User targetUser = getUser(targetAccount.userId(), TARGET);

        if (sourceUser.equals(targetUser)) {
            commission = ZERO;
        }

        synchronized (userService) {
            BigDecimal commissionFees = transfer.multiply(commission);
            transferring(sourceUser, sourceAccount, transfer.add(commissionFees), SOURCE);
            transferring(targetUser, targetAccount, transfer, TARGET);

            // Если комиссия есть, то вносим её на счёт админа
            if (commission.compareTo(ZERO) > 0) {
                User admin = getUser(1, TARGET);
                Account adminAccount = getAccount(1, TARGET);
                transferring(admin, adminAccount, commissionFees, TARGET);
            }
        }
    }

    /**
     * Финансовые операции над счётом пользователя
     *
     * @param user    пользователь счёта
     * @param account счёт пользователя
     * @param amount  сумма списания/внесения
     * @param type    списание-SOURCE / внесение-TARGET
     */
    private void transferring(User user,
                              Account account,
                              BigDecimal amount,
                              AccountType type
    ) {
        try {
            int indexSourceAccount = user.accounts().indexOf(account);
            if (type.equals(SOURCE)) {
                account = accountService.withdraw(account, amount);
            } else if (type.equals(TARGET)) {
                account = accountService.deposit(account, amount);
            }
            user.accounts().set(indexSourceAccount, account);
            userService.save(user);
        } catch (RuntimeException e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    private Account getAccount(int accountId, AccountType accountType) {
        return accountService.find(accountId)
                .orElseThrow(() -> new NoSuchElementException("No such %s account ID:%s%n"
                        .formatted(accountType.name().toLowerCase(), accountId)
                ));
    }

    private User getUser(int userId, AccountType accountType) {
        return userService.find(userId)
                .orElseThrow(() -> new NoSuchElementException(("Data consistency is broken " +
                        "when transfer an %s account for a user ID:%s%n")
                        .formatted(accountType.name().toLowerCase(), userId)
                ));
    }

    /**
     * Типы счёта:
     * {@code SOURCE} - для списания,
     * {@code TARGET} - для внесения
     */
    enum AccountType {
        SOURCE,
        TARGET
    }
}
