package ru.ilya_noize.service;

import ru.ilya_noize.model.Account;
import ru.ilya_noize.model.User;

import java.math.BigDecimal;

public interface AccountService {
    Account find(Long accountId);

    Account create(User user);

    boolean deposit(Account account, BigDecimal deposit);

    boolean transfer(Account sourceAccount, Account targetAccount, BigDecimal transfer);

    boolean withdraw(Account account, BigDecimal withdraw);

    void remove(Account account);
}
