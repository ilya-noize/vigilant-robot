package ru.ilya_noize.model;

import java.util.List;

public class User {
    private final int id;
    private final String login;
    private final List<Account> accounts;

    public User(int id, String login, List<Account> accounts) {
        this.id = id;
        this.login = login;
        this.accounts = accounts;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    @Override
    public String toString() {
        return "User{\tid=%s,\tlogin=%s,\taccount=%s%n}".formatted(id, login, accounts);
    }
}
