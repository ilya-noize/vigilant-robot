package ru.shummi.service;

import ru.shummi.entity.User;

import java.util.Collection;

public interface UserService {
    User create(String login);

    Collection<User> getAll();
}

