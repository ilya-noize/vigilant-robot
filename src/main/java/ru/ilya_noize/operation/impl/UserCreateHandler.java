package ru.ilya_noize.operation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.ilya_noize.listener.IOHandler;
import ru.ilya_noize.model.Account;
import ru.ilya_noize.model.User;
import ru.ilya_noize.operation.OperationHandler;
import ru.ilya_noize.operation.OperationType;
import ru.ilya_noize.service.AccountService;
import ru.ilya_noize.service.UserService;

import java.math.BigDecimal;

@Component
public class UserCreateHandler implements OperationHandler {
    private final IOHandler ioHandler;
    private final UserService userService;
    private final AccountService accountService;
    private final int amount;

    @Autowired
    public UserCreateHandler(
            IOHandler ioHandler,
            UserService userService,
            AccountService accountService,
            @Value("${account.amount}") int amount
    ) {
        this.ioHandler = ioHandler;
        this.userService = userService;
        this.accountService = accountService;
        this.amount = amount;
    }


    @Override
    public OperationType getType() {
        return OperationType.USER_CREATE;
    }

    @Override
    public String perform() {
        String login = ioHandler.getString("Enter login for new user");
        int userId = userService.create(login).id();

        Account account = accountService.create(userId);
        Account deposited = accountService.deposit(account, BigDecimal.valueOf(amount));

        User user = userService.addAccount(deposited);

        return "User created: %s".formatted(user);
    }
}
