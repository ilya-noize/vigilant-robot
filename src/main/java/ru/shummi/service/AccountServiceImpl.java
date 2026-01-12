package ru.shummi.service;

import org.springframework.stereotype.Component;
import ru.shummi.exception.ApplicationException;
import ru.shummi.model.Account;
import ru.shummi.model.User;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class AccountServiceImpl implements AccountService {
    private int counterId = Account.ADMIN_ID;
    private final Map<Integer, Account> accounts = new HashMap<>();

    public AccountServiceImpl() {
    }

    @Override
    public Account create(int userId) {
        if (userId == User.ADMIN_ID && accounts.containsKey(Account.ADMIN_ID)) {
            throw new ApplicationException("You can't create account for administrator");
        }
        int id = counterId++;
        Account account = new Account(id, userId, new BigDecimal("0.00"));
        return save(account);
    }

    @Override
    public Account save(Account entity) {
        accounts.put(entity.id(), entity);
        return entity;
    }

    @Override
    public Optional<Account> find(int accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }

    @Override
    public void remove(int id) {
        if (id == Account.ADMIN_ID) {
            throw new ApplicationException("You can't remove account for administrator");
        }
        accounts.remove(id);
    }

    @Override
    public String getEntitySimpleClassName() {
        return Account.class.getSimpleName();
    }
}
