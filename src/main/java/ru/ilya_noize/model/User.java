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

    public int id() {
        return id;
    }

    public List<Account> accounts() {
        return accounts;
    }

    public boolean addAccount(Account account) {
        return accounts.add(account);
    }

    public void removeAccount(Account account) {
        int index = accounts.indexOf(account);
        accounts.remove(index);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;

        return id == user.id && login.equals(user.login) && accounts.equals(user.accounts);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + login.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("User{");
        sb.append("id=").append(id);
        sb.append(", login='").append(login).append('\'');
        sb.append(", accounts=").append(accounts);
        sb.append('}');
        return sb.toString();
    }
}
