package ru.shummi.service;

import ru.shummi.model.Account;

public interface AccountService extends CrudService<Account> {
    Account create(int userId);
}
