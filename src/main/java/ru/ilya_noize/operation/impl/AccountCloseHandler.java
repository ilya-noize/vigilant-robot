package ru.ilya_noize.operation.impl;

import org.springframework.stereotype.Component;
import ru.ilya_noize.model.Account;
import ru.ilya_noize.model.User;
import ru.ilya_noize.operation.OperationHandler;
import ru.ilya_noize.operation.OperationType;
import ru.ilya_noize.service.AccountService;
import ru.ilya_noize.service.UserService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

@Component
public class AccountCloseHandler implements OperationHandler {
    private final Scanner scanner;
    private final AccountService accountService;
    private final UserService userService;

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
     *
     * Если это единственный счёт с положительным балансом, то удаление невозможно до тех пор,
     * пока баланс счёта не будет равен нулю
     * путём снятия или совершения перевода с учётом комиссии за перевод.
     *
     * Если это не единственный счёт и на закрываемом счёте положительный баланс,
     * то сумма с удаляемого счёта переводится на первый счёт пользователя,
     * иначе просто удаляется.
     */
    @Override
    public void perform() {
        System.out.println("Enter account ID to close:");
        Long accountId = scanner.nextLong();
        Account account = accountService.find(accountId);
        BigDecimal money = account.money();
        User user = userService.find(account.userId());
        List<Account> userAccounts = user.accounts();
        if (userAccounts.size() == 1) {
            if (userAccounts.getFirst().money().compareTo(BigDecimal.ZERO) > 0) {
                System.out.printf("You can't close a single account with ID:" +
                        " %s with a non-empty balance.%n", accountId);
                return;
            }
        }
        if (money.compareTo(BigDecimal.ZERO) > 0) {
            userAccounts.getFirst().depositingMoney(money);
        }
        System.out.printf("Account with ID %s has been closed.", accountId);
        accountService.remove(account);
        userAccounts.remove(account);
        userService.removeAccount();
    }
}
