package ru.shummi.operation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shummi.listener.IOHandler;
import ru.shummi.entity.User;
import ru.shummi.operation.OperationHandler;
import ru.shummi.operation.OperationType;
import ru.shummi.service.UserAccountService;

@Component
public class UserCreateHandler implements OperationHandler {
    private final IOHandler ioHandler;
    private final UserAccountService userAccountService;

    @Autowired
    public UserCreateHandler(
            IOHandler ioHandler,
            UserAccountService userAccountService
    ) {
        this.ioHandler = ioHandler;
        this.userAccountService = userAccountService;
    }

    @Override
    public OperationType getType() {
        return OperationType.USER_CREATE;
    }

    @Override
    public String perform() {
        String login = ioHandler.getString("Enter login for new user");
        User user = userAccountService.registrationUser(login);

        return "User created ID: %s".formatted(user);
    }
}
