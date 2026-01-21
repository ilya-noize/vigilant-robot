package ru.shummi.service;

import ru.shummi.entity.Account;
import ru.shummi.entity.User;

import java.math.BigDecimal;

public interface UserAccountService {
    User registrationUser(String login);

    Account addAccountToUserById(Long userId);

    void closeAccountById(Long accountId);

    void withdrawAccountById(Long accountId, BigDecimal withdraw);

    void depositAccountById(Long accountId, BigDecimal deposit);

    void transferAccountFromIdToId(
            Long sourceAccountId,
            Long targetAccountId,
            BigDecimal transfer
    );
}
