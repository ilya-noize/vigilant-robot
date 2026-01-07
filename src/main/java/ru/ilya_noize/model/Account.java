package ru.ilya_noize.model;

import java.math.BigDecimal;

public final class Account {
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

    public void depositingMoney(BigDecimal amount) {
        money.add(amount);
    }


    public void withdrawMoney(BigDecimal amount) {
        money.subtract(amount);
    }

    @Override
    public String toString() {
        return "Account{id=%s, userId=%s, money=%s}".formatted(id, userId, money);
    }
}
