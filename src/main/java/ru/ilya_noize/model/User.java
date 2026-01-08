package ru.ilya_noize.model;

import java.util.List;

public class User {
    private final Long id;
    private final String login;
    private final List<Account> accounts;

    public User(Long id, String login, List<Account> accounts) {
        this.id = id;
        this.login = login;
        this.accounts = accounts;
    }

    public Long id() {
        return id;
    }

    public String login() {
        return login;
    }

    public List<Account> accounts() {
        return accounts;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;

        return id.equals(user.id) && login.equals(user.login) && accounts.equals(user.accounts);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
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
