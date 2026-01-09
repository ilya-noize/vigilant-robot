package ru.ilya_noize.operation.impl;

import org.springframework.beans.factory.annotation.Autowired;
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

@Component
public class AccountCloseHandler implements OperationHandler {
    private final IOHandler ioHandler;
    private final AccountService accountService;
    private final UserService userService;

    @Autowired
    public AccountCloseHandler(
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
        return OperationType.ACCOUNT_CLOSE;
    }

    /**
     * Закрытие счёта.
     * <p>
     * Если этот счёт с положительным балансом, то удаление невозможно до тех пор,
     * пока баланс счёта не будет равен нулю
     * путём снятия или совершения перевода с учётом комиссии за перевод.
     * @return Сообщение для {@code ConsoleListener.update()}
     */
    @Override
    public String perform() {
        int accountId = ioHandler.getInteger("Enter account ID to close");
        Account account = accountService.find(accountId)
                .orElseThrow(() -> new IllegalArgumentException("No such account with ID:%s%n"
                        .formatted(accountId)));

        if (account.money().compareTo(BigDecimal.ZERO) == 0) {
            throw new ApplicationException("Account ID: %s not empty".formatted(accountId));
        }

        int userId = account.userId();
        User user = userService.find(userId)
                .orElseThrow(() -> new IllegalStateException(("Data consistency is broken " +
                        "when closing an account ID:%s for a user ID:%s%n")
                        .formatted(accountId, userId)
                ));

        accountService.remove(accountId);
        user.removeAccount(account);

        return "Account with ID %s has been closed.".formatted(accountId);
    }
}
