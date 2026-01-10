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

import static java.math.BigDecimal.ZERO;

/**
 * Перевод между счетами пользователя(ей)
 */
@Component
public class AccountTransferHandler implements OperationHandler {
    private final IOHandler ioHandler;
    private final BigDecimal commission;
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
        this.commission = new BigDecimal(commission == null ? "0" : commission);
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
        accountTransfer(sourceAccountId, targetAccountId, amount);

        return "Amount %s transferred from account ID: %s to account ID: %s"
                .formatted(amount, sourceAccountId, targetAccountId);
    }

    /**
     * Финансовые операции над счётом пользователя
     *
     * @param sourceAccountId номер счёта списания
     * @param targetAccountId номер счёта внесения
     * @param amount          сумма списания/внесения
     */
    public void accountTransfer(
            int sourceAccountId,
            int targetAccountId,
            String amount
    ) {
        BigDecimal transfer = new BigDecimal(amount);
        if (transfer.compareTo(ZERO) < 1) {
            throw new ApplicationException("Amount must be positive");
        }

        Account sourceAccount = accountService.get(sourceAccountId);
        Account targetAccount = accountService.get(targetAccountId);
        Account adminAccount = accountService.get(Account.ADMIN_ID);

        BigDecimal commissionFee = transfer.multiply(
                sourceAccount.userId() == targetAccount.userId() ? ZERO : commission
        );
        sourceAccount.withdrawMoney(transfer.add(commissionFee));
        targetAccount.depositMoney(transfer);
        adminAccount.depositMoney(commissionFee);

        updateUserAccount(sourceAccount);
        updateUserAccount(targetAccount);
        updateUserAccount(adminAccount);
    }

    private void updateUserAccount(Account account) {
        User user = userService.get(account.userId());
        user.updateAccount(account);
    }
}
