package ru.ilya_noize.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.ilya_noize.model.Account;
import ru.ilya_noize.model.User;

import java.math.BigDecimal;
import java.util.*;

@Component
public class UserServiceImpl implements UserService {
    private Long counterId = 0L;
    private final BigDecimal amount;
    private final AccountService accountService;
    private final Set<String> logins = new HashSet<>();
    private final Map<Long, User> users = new HashMap<>();

    @Autowired
    public UserServiceImpl(
            AccountService accountService,
            @Value(value = "${account.amount}") String amount
    ) {
        this.amount = new BigDecimal(amount);
        this.accountService = accountService;
    }

    @Override
    public User create(String login) {
        if (!logins.add(login)) {
            throw new IllegalArgumentException("Rejected command:" +
                    " Login:%s must be unique.".formatted(login));
        }
        Long id = ++counterId;
        User user = new User(id, login, new ArrayList<>());
        Account account = accountService.create(user);
        account.depositMoney(amount);
        accountService.save(account);
        user.accounts().add(account);
        System.out.println("account = " + account);
        System.out.println("user = " + user);
        return this.save(user);
    }

    @Override
    public User save(User user) {
        User put = users.put(user.id(), user);
        System.out.printf("%s saved.%n", put);
        return put;
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Optional<User> find(Long userId) {
        if (!users.containsKey(userId)) {
            return Optional.empty();
        }
        return Optional.of(users.get(userId));
    }
}
