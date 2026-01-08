package ru.ilya_noize.service;

import ru.ilya_noize.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {
    User create(String login);

    User save(User user);

    Collection<User> getAll();

    Optional<User> find(Long userId);
}

