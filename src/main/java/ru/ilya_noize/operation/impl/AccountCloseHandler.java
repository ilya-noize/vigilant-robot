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
public class AccountCloseHandler implements OperationHandler {
    private final Scanner scanner;
    private final AccountService accountService;
    private final UserService userService;

    @Autowired
    public AccountCloseHandler(Scanner scanner,
                               AccountService accountService,
                               UserService userService) {
        this.scanner = scanner;
        this.accountService = accountService;
        this.userService = userService;
    }

    @Override
    public OperationType getType() {
        return OperationType.ACCOUNT_CLOSE;
    }

    /**
     * Закрытие счёта.
     * <p>
     * Если это единственный счёт с положительным балансом, то удаление невозможно до тех пор,
     * пока баланс счёта не будет равен нулю
     * путём снятия или совершения перевода с учётом комиссии за перевод.
     * <p>
     * Если это не единственный счёт и на закрываемом счёте положительный баланс,
     * то сумма с удаляемого счёта переводится на первый счёт пользователя,
     * иначе просто удаляется.
     */
    @Override
    public void perform() {
        System.out.println("Enter account ID to close:");
        Long accountId = scanner.nextLong();
        Account account = accountService.find(accountId)
                .orElseThrow(() -> new IllegalArgumentException("No such account with ID:%s%n"
                        .formatted(accountId)));

        BigDecimal money = account.money();
        Long userId = account.userId();
        User user = userService.find(userId)
                .orElseThrow(() -> new IllegalArgumentException(("Data consistency is broken " +
                        "when closing an account ID:%s for a user ID:%s%n")
                        .formatted(accountId, userId)));

        Account mainAccount = user.accounts().getFirst();
        if (mainAccount.id().equals(accountId) && mainAccount.money().compareTo(BigDecimal.ZERO) > 0) {
            System.out.printf("Error: account ID:%s closure rejected.%n", accountId);
            throw new IllegalStateException("An attempt to close a single account with a positive balance.%n" +
                    "To complete the operation successfully, " +
                    "select the withdrawal of money or transfer of money to another account.");
        }

        if (money.compareTo(BigDecimal.ZERO) > 0) {
            account.withdrawMoney(money);
            mainAccount.depositMoney(money);
        }

        user.accounts().remove(account);
        user.accounts().addFirst(mainAccount);
        userService.save(user);
        accountService.remove(account);
        System.out.printf("Account with ID %s has been closed.", accountId);
    }
}
