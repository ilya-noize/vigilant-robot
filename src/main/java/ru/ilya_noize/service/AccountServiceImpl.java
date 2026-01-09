package ru.ilya_noize.service;

import org.springframework.stereotype.Component;
import ru.ilya_noize.exception.ApplicationException;
import ru.ilya_noize.model.Account;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class AccountServiceImpl implements AccountService {
    private int counterId = 1;
    private final Map<Integer, Account> accounts = new HashMap<>();

    public AccountServiceImpl() {
        Account serviceAccount = new Account(counterId++, 1, "0");
        accounts.put(serviceAccount.id(), serviceAccount);
    }

    @Override
    public Account create(int userId) {
        int id = counterId++;
        Account account = new Account(id, userId, "0");
        accounts.put(id, account);
        return account;
    }

    @Override
    public Account save(Account account) {
        return accounts.put(account.id(), account);
    }

    @Override
    public Optional<Account> find(int accountId) {
        if (!accounts.containsKey(accountId)) {
            return Optional.empty();
        }
        return Optional.of(accounts.get(accountId));
    }

    /**
     * @param account Счёт пользователя
     * @param deposit Сумма зачисления
     * @return Сохранённый счёт
     * @throws ApplicationException Отрицательная сумма зачисления
     */
    @Override
    public Account deposit(Account account, BigDecimal deposit) {
        if (deposit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApplicationException("Amount %s reject depositing from account ID:%s"
                    .formatted(deposit, account.id()));
        }
        account.depositMoney(deposit);
        return save(account);
    }

    /**
     * @param account  Счёт пользователя
     * @param withdraw Сумма списания
     * @return Сохранённый счёт
     * @throws ApplicationException Не достаточно средств
     */
    @Override
    public Account withdraw(Account account, BigDecimal withdraw) {
        if (withdraw.compareTo(account.money()) > 0) {
            throw new ApplicationException((("There are not enough funds to withdraw from" +
                    " the account ID:%s").formatted(account.id())));
        }
        account.withdrawMoney(withdraw);
        return save(account);
    }

    @Override
    public void remove(int id) {
        if (accounts.containsKey(id)) {
            if(accounts.get(id).money().compareTo(BigDecimal.ZERO) != 0) {
                throw new ApplicationException("Account ID: %s not empty".formatted(id));
            }
            accounts.remove(id);
        } else {
            throw new ApplicationException("No such account ID: %s".formatted(id));
        }
    }
}
