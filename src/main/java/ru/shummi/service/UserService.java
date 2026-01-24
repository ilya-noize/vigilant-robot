package ru.shummi.service;

import ru.shummi.entity.User;

import java.util.List;

public interface UserService {
    User create(String login);

    List<User> getAll();
}

