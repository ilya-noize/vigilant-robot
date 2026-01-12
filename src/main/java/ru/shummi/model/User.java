package ru.shummi.model;

import ru.shummi.exception.ApplicationException;

import java.util.NoSuchElementException;
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

    public void updateAccount(Account account) {
        accounts.remove(account);
        accounts.add(account);
    }

    public Account getFirstAccountNotEqualsId(int id) {
        return accounts.stream()
                .filter(account -> account.id() != id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "Can't find first account by user ID:%s".formatted(id())
                ));
    }

    /**
     * Закрытие счёта
     *
     * @param account Closeable account
     * @throws ApplicationException You can't close account for administrator
     * @throws ApplicationException There is money in the account
     * @throws ApplicationException You can't close single account
     */
    public void removeAccount(Account account) {
        if (account.id() == Account.ADMIN_ID) {
            throw new ApplicationException("You can't close account for administrator");
        }
        if (isSingleAccount()) {
            throw new ApplicationException("You can't close single " +
                    "account for user ID:%s".formatted(id()));
        }
        accounts.remove(account);
    }

    public boolean isSingleAccount() {
        return accounts.size() == 1;
    }

    public boolean haveAccounts() {
        return !accounts.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;

        return id == user.id && login.equals(user.login);
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
