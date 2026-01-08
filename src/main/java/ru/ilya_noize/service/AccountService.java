package ru.ilya_noize.service;

import ru.ilya_noize.model.Account;
import ru.ilya_noize.model.User;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountService {
    Account create(User user);

    Account save(Account deposited);

    Optional<Account> find(Long accountId);

    Account deposit(Account account, BigDecimal deposit);

    Account withdraw(Account account, BigDecimal withdraw);

    void remove(Account account);
}
