package ru.shummi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "login",
            unique = true,
            nullable = false,
            updatable = false
    )
    private String login;

    @Column(
            name = "role",
            nullable = false,
            updatable = false
    )
    @Enumerated(value = EnumType.STRING)
    private UserRole role = UserRole.USER_ROLE;

    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.EAGER
    )
    private List<Account> accounts = new ArrayList<>();

    public User() {
    }

    public User(String login) {
        this.login = login;
    }

    public Long id() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String login() {
        return login;
    }

    @Deprecated(forRemoval = true)
    public void setLogin(String login) {
        this.login = login;
    }

    public List<Account> accounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public UserRole role() {
        return role;
    }

    @Deprecated(forRemoval = true)
    public void setRole(String role) {
        this.role = UserRole.valueOf(role);
    }

//    public Account getFirstAccountNotEqualsId(int id) {
//        return accounts.stream()
//                .filter(account -> account.id() != id)
//                .findFirst()
//                .orElseThrow(() -> new NoSuchElementException(
//                        "Can't find first account by user ID:%s".formatted(id())
//                ));
//    }

//    /**
//     * Закрытие счёта
//     *
//     * @param account Closeable account
//     * @throws ApplicationException You can't close account for administrator
//     * @throws ApplicationException There is money in the account
//     * @throws ApplicationException You can't close single account
//     */
//    public void removeAccount(ru.shummi.model.Account account) {
//        if (account.id() == Account.ADMIN_ID) {
//            throw new ApplicationException("You can't close account for administrator");
//        }
//        if (isSingleAccount()) {
//            throw new ApplicationException("You can't close single " +
//                    "account for user ID:%s".formatted(id()));
//        }
//        accounts.remove(account);
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id) && login.equals(user.login);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + login.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "User {" + "id=" + id() +
                ", login='" + login() + '\'' +
                ", accounts=" + accounts() +
                '}';
    }
}
