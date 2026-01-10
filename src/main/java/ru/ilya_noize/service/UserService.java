package ru.ilya_noize.service;

import ru.ilya_noize.model.User;

import java.util.Collection;

public interface UserService extends CrudService<User> {
    Collection<User> getAll();

    boolean notExists(int userId);

    boolean isEmpty();
}

