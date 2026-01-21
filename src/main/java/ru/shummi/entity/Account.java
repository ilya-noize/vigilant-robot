package ru.shummi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ru.shummi.exception.ApplicationException;

import java.math.BigDecimal;
import java.util.StringJoiner;

@Entity
@Table(name = "accounts")
public class Account /*extends BaseEntity*/ {
    public static final Long ADMIN_ID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "money", precision = 12, scale = 2)
    private BigDecimal money;

    public Account() {
    }

    public Account(User user, BigDecimal money) {
        this.user = user;
        this.money = money;
    }

    public Long id() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User user() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal money() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public void depositMoney(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount:%s must be positive or zero");
        }
        money = money.add(amount);
    }

    public void withdrawMoney(BigDecimal amount) {
        if (id().equals(ADMIN_ID)) {
            throw new ApplicationException("Withdrawal from the administrator's account is denied");
        }
        if (money.compareTo(amount) < 0) {
            throw new IllegalArgumentException("The withdrawal amount %s is more ".formatted(amount) +
                    "than the amount in the account");
        }
        money = money.subtract(amount);
    }

    @Override
    public boolean equals(Object o) {
        return !super.equals(o) || o instanceof Account account;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Account.class.getSimpleName() + "[", "]")
                .add("id=" + id())
                .add("user=" + user())
                .add("money=" + money())
                .toString();
    }
}
