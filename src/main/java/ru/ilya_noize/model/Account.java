package ru.ilya_noize.model;

import java.math.BigDecimal;

public class Account {
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
        money = money.add(amount);
    }

    public Account withdrawMoney(BigDecimal amount) {
        if(money.compareTo(amount) < 0) {
            throw new IllegalArgumentException("The withdrawal amount:%s is more ".formatted(amount) +
                    "than the amount in the account%n");
        }
        money = money.subtract(amount);
        return this;
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
