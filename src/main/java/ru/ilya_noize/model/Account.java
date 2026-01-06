package ru.ilya_noize.model;

public class Account {
    private final int id;
    private final int userId;
    private int money;

    public Account(int id, int userId, int money) {
        this.id = id;
        this.userId = userId;
        this.money = money;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getMoney() {
        return money;
    }

    public void depositingMoney(int money) {
        this.money += money;
    }

    public void debitingMoney(int money) {
        this.money -= money;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", userId=" + userId +
                ", money=" + money +
                '}';
    }
}
