package ru.shummi.service.memoryImpl;

import org.springframework.stereotype.Component;
import ru.shummi.exception.ApplicationException;
import ru.shummi.model.User;
import ru.shummi.service.UserService;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static ru.shummi.model.User.ADMIN_ID;

@Component
public class UserServiceMemoryImpl implements UserService {
    private int counterId = ADMIN_ID;
    private final Set<String> logins = new HashSet<>();
    private final Map<Integer, User> users = new HashMap<>();

    public UserServiceMemoryImpl() {
    }

    @Override
    public User create(String login) {
        if (!logins.add(login)) {
            throw new ApplicationException("Rejected: login %s is used. Try another login"
                    .formatted(login));
        }
        int userId = counterId++;
        User user = new User(userId, login, new HashSet<>());
        return save(user);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User save(User entity) {
        users.put(entity.id(), entity);
        return entity;
    }

    @Override
    public Optional<User> find(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void remove(int id) {
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
    }

    @Override
    public String getEntitySimpleClassName() {
        return User.class.getSimpleName();
    }

    @Override
    public boolean isEmpty() {
        return users.size() == 1;
    }
}
