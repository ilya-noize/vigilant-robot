package ru.ilya_noize.service;

import org.springframework.stereotype.Component;
import ru.ilya_noize.model.Account;
import ru.ilya_noize.model.User;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class AccountServiceImpl implements AccountService {
    private Long counterId = 0L;
    private final Map<Long, Account> accounts = new HashMap<>();

    public AccountServiceImpl() {
    }

    @Override
    public Account create(User user) {
        Long id = ++counterId;
        Account account = new Account(id, user.id(), "0");
        return accounts.put(id, account);
    }

    @Override
    public Account save(Account account) {
        return accounts.put(account.id(), account);
    }

    @Override
    public Optional<Account> find(Long accountId) {
        if (!accounts.containsKey(accountId)) {
            return Optional.empty();
        }
        return Optional.of(accounts.get(accountId));
    }

    @Override
    public Account deposit(Account account, BigDecimal deposit) {
        account.depositMoney(deposit);
        return save(account);
    }

    @Override
    public Account withdraw(Account account, BigDecimal withdraw) {
        account.withdrawMoney(withdraw);
        return save(account);
    }

    @Override
    public void remove(Account account) {
        accounts.remove(account.id(), account);
    }
}
