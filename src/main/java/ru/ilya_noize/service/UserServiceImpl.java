package ru.ilya_noize.service;

import org.springframework.stereotype.Component;
import ru.ilya_noize.exception.ApplicationException;
import ru.ilya_noize.model.User;

import java.util.*;

import static ru.ilya_noize.model.User.ADMIN_ID;

@Component
public class UserServiceImpl implements UserService {
    private int counterId = ADMIN_ID;
    private final Set<String> logins = new HashSet<>();
    private final Map<Integer, User> users = new HashMap<>();

    public UserServiceImpl() {
    }

    @Override
    public User create(Object param) {
        String login = (String) param;
        if (!logins.add(login)) {
            throw new ApplicationException("Rejected: login %s is used. Try another login"
                    .formatted(login));
        }
        int userId = counterId++;
        User user = new User(userId, login, new HashSet<>());
        users.put(user.id(), user);
        return user;
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Optional<User> find(int id) {
        if (notExists(id)) {
            return Optional.empty();
        }
        return Optional.of(users.get(id));
    }

    @Override
    public boolean remove(int id) {
        if (id == ADMIN_ID) {
            throw new ApplicationException(("Remove user ID: %s ".formatted(id) +
                    "rejected: it's administrator"));
        }
        User user = get(id);
        if (user.haveAccounts()) {
            throw new ApplicationException(("Remove user ID: %s ".formatted(id) +
                    "rejected: user have accounts"));
        }
        users.remove(id);
        return true;
    }

    @Override
    public String getEntitySimpleClassName() {
        return User.class.getSimpleName();
    }

    @Override
    public boolean notExists(int id) {
        return !users.containsKey(id);
    }

    @Override
    public boolean isEmpty() {
        return users.size() == 1;
    }
}
