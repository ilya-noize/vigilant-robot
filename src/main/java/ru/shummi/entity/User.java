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
import java.util.StringJoiner;

@Entity
@Table(name = "usr")
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
    private final List<Account> accounts = new ArrayList<>();

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

    public UserRole role() {
        return role;
    }

    public void setRole(String role) {
        this.role = UserRole.valueOf(role);
    }

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
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("login='" + login + "'")
                .add("role=" + role)
                .add("accounts=" + accounts)
                .toString();
    }
}
