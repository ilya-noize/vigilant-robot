package ru.ilya_noize.service;

import ru.ilya_noize.model.Account;
import ru.ilya_noize.model.User;

import java.util.Collection;

public interface UserService extends CrudService<User> {
    User create(String login);

    User addAccount(Account account);

    User removeAccount(Account account);

    Collection<User> getAll();

    boolean notExists(int userId);

    boolean usersIsEmpty();
}

