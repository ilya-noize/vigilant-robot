package ru.shummi.service;

import ru.shummi.model.User;

import java.util.Collection;

public interface UserService extends CrudService<User> {
    User create(String login);

    Collection<User> getAll();

    boolean isEmpty();
}

