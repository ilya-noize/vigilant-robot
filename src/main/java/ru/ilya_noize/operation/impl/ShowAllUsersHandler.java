package ru.ilya_noize.operation.impl;

import org.springframework.stereotype.Component;
import ru.ilya_noize.model.User;
import ru.ilya_noize.operation.OperationHandler;
import ru.ilya_noize.operation.OperationType;
import ru.ilya_noize.service.UserService;

import java.util.Collection;

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
    public String perform() {
        Collection<User> users = userService.getAll();
        if (users.isEmpty()) {
            return "List empty";
        }
        System.out.println("┌───────────────────────────");
        users.forEach(user ->
                System.out.printf("│ %s%n", user)
        );
        System.out.println("└───────────────────────────");
        int size = users.size();
        return "%s user%s".formatted(size, size > 1 ? "s" : "");
    }
}
