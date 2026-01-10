package ru.ilya_noize.service;

import org.springframework.stereotype.Component;
import ru.ilya_noize.exception.ApplicationException;
import ru.ilya_noize.model.Account;
import ru.ilya_noize.model.User;

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
    public Account create(Object param) {
        int userId = (int) param;
        if (userId == User.ADMIN_ID && accounts.containsKey(Account.ADMIN_ID)) {
            throw new ApplicationException("You can't create account for administrator");
        }
        int id = counterId++;
        Account account = new Account(id, userId, "0.00");
        accounts.put(id, account);
        return account;
    }

    @Override
    public Optional<Account> find(int accountId) {
        if (!accounts.containsKey(accountId)) {
            return Optional.empty();
        }
        return Optional.of(accounts.get(accountId));
    }

    @Override
    public boolean remove(int id) {
        if (id == Account.ADMIN_ID) {
            throw new ApplicationException("You can't remove account for administrator");
        }
        Account account = get(id);
        if (account.money().compareTo(BigDecimal.ZERO) > 0) {
            throw new ApplicationException("Account ID: %s not empty".formatted(id));
        }
        accounts.remove(id);
        return true;
    }

    @Override
    public String getEntitySimpleClassName() {
        return Account.class.getSimpleName();
    }
}
