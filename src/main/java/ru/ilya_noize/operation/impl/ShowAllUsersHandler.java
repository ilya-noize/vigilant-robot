package ru.ilya_noize.operation.impl;

import org.springframework.stereotype.Component;
import ru.ilya_noize.operation.OperationHandler;
import ru.ilya_noize.operation.OperationType;
import ru.ilya_noize.service.UserService;

@Component
public class ShowAllUsersHandler implements OperationHandler {
    private final UserService userService;

    public ShowAllUsersHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OperationType getType() {
        return OperationType.SHOW_ALL_USERS;
    }

    @Override
    public void perform() {
        userService.getAll().forEach(System.out::println);
    }
}
