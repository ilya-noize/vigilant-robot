package ru.ilya_noize.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ilya_noize.model.Account;
import ru.ilya_noize.model.User;

@Component
public class UserAccountService {
    private final UserService userService;
    private final AccountService accountService;

    @Autowired
    public UserAccountService(
            UserService userService,
            AccountService accountService
    ) {
        this.userService = userService;
        this.accountService = accountService;
        createAdminAccount();
    }

    private void createAdminAccount() {
        User admin = userService.create("admin");
        Account account = accountService.create(User.ADMIN_ID);
        admin.addAccount(account);
    }
}
