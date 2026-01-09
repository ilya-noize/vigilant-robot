package ru.ilya_noize.service;

import org.springframework.stereotype.Component;
import ru.ilya_noize.exception.ApplicationException;
import ru.ilya_noize.model.Account;
import ru.ilya_noize.model.User;

import java.util.*;

@Component
public class UserServiceImpl implements UserService {
    private int counterId = 1;
    private final Set<String> logins = new HashSet<>();
    private final Map<Integer, User> users = new HashMap<>();

    public UserServiceImpl() {
        String login = "admin";
        logins.add(login);
        User admin = new User(counterId++, login, new ArrayList<>());
        Account serviceAccount = new Account(1, admin.id(), "0");
        admin.addAccount(serviceAccount);
        users.put(admin.id(), admin);
    }

    @Override
    public User create(String login) {
        if (!logins.add(login)) {
            throw new ApplicationException("Rejected: login %s is used. Try another login"
                    .formatted(login));
        }
        int userId = counterId++;
        User user = new User(userId, login, new ArrayList<>());
        return save(user);
    }

    @Override
    public User save(User user) {
        users.put(user.id(), user);
        return user;
    }

    @Override
    public User addAccount(Account account) {
        User user = users.get(account.userId());
        if (user.accounts().contains(account)) {
            int index = user.accounts().indexOf(account);
            user.accounts().set(index, account);
        } else {
            user.accounts().add(account);
        }
        return save(user);
    }

    @Override
    public User removeAccount(Account account) {
        int userId = account.userId();
        if (notExists(userId)) {
            throw new ApplicationException("No such user ID:%s".formatted(userId));
        }
        User user = users.get(userId);
        List<Account> accounts = user.accounts();

        if (accounts.isEmpty()) {
            throw new ApplicationException("No accounts for user ID:%s".formatted(userId));
        }
        user.removeAccount(account);
        return user;
    }


    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Optional<User> find(int userId) {
        if (notExists(userId)) {
            return Optional.empty();
        }
        return Optional.of(users.get(userId));
    }

    @Override
    public void remove(int id) {
        if (notExists(id)) {
            throw new ApplicationException("No such user ID: %s".formatted(id));
        }
        if (id == 1) {
            throw new ApplicationException("Remove user rejected: it's administrator");
        }
        if (!users.get(id).accounts().isEmpty()) {
            throw new ApplicationException("Remove user rejected: user has accounts");
        }
        users.remove(id);
    }

    @Override
    public boolean notExists(int userId) {
        return !users.containsKey(userId);
    }

    @Override
    public boolean usersIsEmpty() {
        return users.isEmpty();
    }
}
