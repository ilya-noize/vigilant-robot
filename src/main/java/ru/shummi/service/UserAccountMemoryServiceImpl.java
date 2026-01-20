package ru.shummi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.shummi.exception.ApplicationException;
import ru.shummi.model.Account;
import ru.shummi.model.User;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

@Component
public class UserAccountMemoryServiceImpl implements UserAccountService {
    private final UserService userService;
    private final AccountService accountService;
    private final BigDecimal commission;
    private final BigDecimal amount;

    @Autowired
    public UserAccountMemoryServiceImpl(
            UserService userService,
            AccountService accountService,
            @Value("${account.commission}")
            String commission,
            @Value("${account.amount}")
            String amount
    ) {
        this.userService = userService;
        this.accountService = accountService;
        this.commission = new BigDecimal(commission == null ? "0.00" : commission);
        this.amount = new BigDecimal(amount == null ? "0.00" : amount);
        registrationUser("admin");

    }

    @Override
    public User registrationUser(String login) {
        User user = userService.create(login);
        Account account = accountService.create(user.id());
        if(user.id() != User.ADMIN_ID) {
            account.depositMoney(amount);
        }
        user.addAccount(account);
        return user;
    }

    @Override
    public int addAccountToUserById(int userId) {
        if (userService.isEmpty()) {
            throw new ApplicationException("The list of users is empty. " +
                    "Creating an account is not possible.");
        }
        Account account = accountService.create(userId);
        User user = userService.get(userId);
        user.addAccount(account);
        return account.id();
    }

    /**
     * Закрытие счёта.
     * <p>
     * Если этот счёт с положительным балансом,
     * то удаление возможно только когда баланс счёта не будет равен нулю
     * путём совершения перевода на первый счёт пользователя.
     *
     * @throws ApplicationException             если удаляется счёт администратора
     * @throws ApplicationException             если удаляется единственный счёт
     * @throws java.util.NoSuchElementException если счёт не найден
     * @throws java.util.NoSuchElementException если другой счёт не найден
     * @throws java.util.NoSuchElementException если пользователь не найден
     */
    @Override
    public void closeAccountById(int accountId) {
        if (accountId == Account.ADMIN_ID) {
            throw new ApplicationException("You can't remove account for administrator");
        }
        Account account = accountService.get(accountId);
        User user = userService.get(account.userId());
        if (user.isSingleAccount()) {
            throw new ApplicationException("You can't remove single account");
        }
        BigDecimal money = account.money();
        if (money.compareTo(BigDecimal.ZERO) > 0) {
            Account firstAccount = user.getFirstAccountNotEqualsId(accountId);
            firstAccount.depositMoney(money);
            user.updateAccount(firstAccount);
            accountService.remove(accountId);
            user.removeAccount(account);
        }
    }

    /**
     * Финансовые операции (снятие) над счётом пользователя
     *
     * @param accountId номер счёта
     * @param withdraw  сумма
     * @throws ApplicationException             Amount must be positive
     * @throws java.util.NoSuchElementException если счёт не найден
     * @throws java.util.NoSuchElementException если пользователь не найден
     */
    @Override
    public void withdrawAccountById(int accountId, BigDecimal withdraw) {
        Account account = accountService.get(accountId);
        User user = userService.get(account.userId());

        validatePositiveMoney(withdraw);
        account.withdrawMoney(withdraw);
        user.updateAccount(account);
    }

    /**
     * Финансовые операции (пополнение) над счётом пользователя
     *
     * @param accountId номер счёта
     * @param deposit   сумма
     * @throws ApplicationException             Amount must be positive
     * @throws java.util.NoSuchElementException если счёт не найден
     * @throws java.util.NoSuchElementException если пользователь не найден
     */
    @Override
    public void depositAccountById(int accountId, BigDecimal deposit) {
        validatePositiveMoney(deposit);
        Account account = accountService.get(accountId);
        User user = userService.get(account.userId());
        account.depositMoney(deposit);
        user.updateAccount(account);
    }


    /**
     * Финансовые операции (перевод) над счётом пользователя
     *
     * @param sourceAccountId номер счёта списания
     * @param targetAccountId номер счёта внесения
     * @param transfer        сумма списания/внесения
     * @throws ApplicationException             Amount must be positive
     * @throws java.util.NoSuchElementException если счёт не найден
     * @throws java.util.NoSuchElementException если пользователь не найден
     */
    @Override
    public void transferAccountFromIdToId(
            int sourceAccountId,
            int targetAccountId,
            BigDecimal transfer
    ) {
        validatePositiveMoney(transfer);

        Account sourceAccount = accountService.get(sourceAccountId);
        Account targetAccount = accountService.get(targetAccountId);
        Account adminAccount = accountService.get(Account.ADMIN_ID);

        BigDecimal commissionFee = transfer.multiply(
                sourceAccount.userId() == targetAccount.userId() ? ZERO : commission
        );
        sourceAccount.withdrawMoney(transfer.add(commissionFee));
        targetAccount.depositMoney(transfer);
        adminAccount.depositMoney(commissionFee);

        updateUserAccount(sourceAccount);
        updateUserAccount(targetAccount);
        updateUserAccount(adminAccount);
    }

    private void validatePositiveMoney(BigDecimal money) {
        if (money.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApplicationException("Amount must be positive");
        }
    }

    private void updateUserAccount(Account account) {
        User user = userService.get(account.userId());
        user.updateAccount(account);
    }
}
