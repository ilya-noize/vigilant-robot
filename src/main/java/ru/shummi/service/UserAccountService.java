package ru.shummi.service;

import ru.shummi.model.User;

import java.math.BigDecimal;

public interface UserAccountService {
    User registrationUser(String login);

    int addAccountToUserById(int userId);

    void closeAccountById(int accountId);

    void withdrawAccountById(int accountId, BigDecimal withdraw);

    void depositAccountById(int accountId, BigDecimal deposit);

    void transferAccountFromIdToId(
            int sourceAccountId,
            int targetAccountId,
            BigDecimal transfer
    );
}
