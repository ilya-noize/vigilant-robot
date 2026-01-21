package ru.shummi.operation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shummi.entity.User;
import ru.shummi.operation.OperationHandler;
import ru.shummi.operation.OperationType;
import ru.shummi.service.UserService;

import java.util.List;

@Component
public class ShowAllUsersHandler implements OperationHandler {
    private final UserService userService;

    @Autowired
    public ShowAllUsersHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OperationType getType() {
        return OperationType.SHOW_ALL_USERS;
    }

    @Override
    public String perform() {
        List<User> users = userService.getAll();
        System.out.println("┌───────────────────────────");
        users.forEach(user ->
                System.out.printf("│ %s%n", user)
        );
        System.out.println("└───────────────────────────");
        if (users.isEmpty()) return "List empty";

        int size = users.size();
        return "%s user%s".formatted(size, size > 1 ? "s" : "");
    }
}
