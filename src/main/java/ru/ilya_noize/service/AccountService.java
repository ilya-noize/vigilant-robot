package ru.ilya_noize.service;

import ru.ilya_noize.model.Account;

import java.math.BigDecimal;

public interface AccountService extends CrudService<Account> {
    Account create(int userId);

    Account deposit(Account account, BigDecimal deposit);

    Account withdraw(Account account, BigDecimal withdraw);
}
