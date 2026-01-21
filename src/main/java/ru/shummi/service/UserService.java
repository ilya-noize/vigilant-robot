package ru.shummi.service;

import ru.shummi.entity.User;

import java.util.List;

public interface UserService {
    boolean isUniqueLogin(String login);

    User create(String login);

    List<User> getAll();
}

