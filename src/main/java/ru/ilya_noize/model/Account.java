package ru.ilya_noize.model;

import ru.ilya_noize.exception.ApplicationException;

import java.math.BigDecimal;

public class Account {
    public static final int ADMIN_ID = 1;
    private final int id;
    private final int userId;
    private BigDecimal money;

    public Account(int id, int userId, String money) {
        this.id = id;
        this.userId = userId;
        this.money = new BigDecimal(money);
    }

    public int id() {
        return id;
    }

    public int userId() {
        return userId;
    }

    public BigDecimal money() {
        return money;
    }

    public void depositMoney(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount:%s must be positive or zero");
        }
        money = money.add(amount);
    }

    public void withdrawMoney(BigDecimal amount) {
        if (id == ADMIN_ID) {
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
        if (!(o instanceof Account account)) return false;

        return id == account.id && userId == account.userId;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + userId;
        return result;
    }

    @Override
    public String toString() {
        return "Account{id=%s, userId=%s, money=%s}".formatted(id, userId, money);
    }
}
