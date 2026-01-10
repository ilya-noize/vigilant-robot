package ru.ilya_noize.model;

import ru.ilya_noize.exception.ApplicationException;

import java.util.Set;

public class User {
    public static final int ADMIN_ID = 1;
    private final int id;
    private final String login;
    private final Set<Account> accounts;

    public User(int id, String login, Set<Account> accounts) {
        this.id = id;
        this.login = login;
        this.accounts = accounts;
    }

    public int id() {
        return id;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void updateAccount(Account account){
        accounts.remove(account);
        accounts.add(account);
    }

    public boolean removeAccount(Account account) {
        if (account.id() == Account.ADMIN_ID) {
            throw new ApplicationException("You can't remove account for administrator");
        }
        return accounts.remove(account);
    }

    public boolean haveAccounts() {
        return !accounts.isEmpty();
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
        return "User {" + "id=" + id +
                ", login='" + login + '\'' +
                ", accounts=" + accounts +
                '}';
    }
}
