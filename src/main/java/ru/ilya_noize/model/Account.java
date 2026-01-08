package ru.ilya_noize.model;

import java.math.BigDecimal;

public class Account {
    private final Long id;
    private final Long userId;
    private final BigDecimal money;

    public Account(Long id, Long userId, String money) {
        this.id = id;
        this.userId = userId;
        this.money = new BigDecimal(money);
    }

    public Long id() {
        return id;
    }

    public Long userId() {
        return userId;
    }

    public BigDecimal money() {
        return money;
    }

    public void depositMoney(BigDecimal amount) {
        money.add(amount);
    }

    public Account withdrawMoney(BigDecimal amount) {
        if(money.compareTo(amount) <= 0) {
            throw new IllegalArgumentException("The withdrawal amount:%s is more ".formatted(amount) +
                    "than the amount in the account%n");
        }
        money.subtract(amount);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Account account)) return false;

        return id.equals(account.id) && userId.equals(account.userId);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + userId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Account{id=%s, userId=%s, money=%s}".formatted(id, userId, money);
    }
}
