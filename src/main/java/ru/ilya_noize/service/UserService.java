package ru.ilya_noize.service;

import ru.ilya_noize.model.User;

import java.util.List;

public interface UserService {
    User create(String login);

    List<User> getAll();

    User find(Long userId);

    List<User> findByIds(List<Long> ids);

    void removeAccount();
}

