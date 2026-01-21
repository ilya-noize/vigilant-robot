package ru.shummi.service.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shummi.entity.Account;
import ru.shummi.entity.User;
import ru.shummi.entity.UserRole;
import ru.shummi.exception.ApplicationException;
import ru.shummi.service.AccountService;
import ru.shummi.service.TransactionExecutorService;
import ru.shummi.service.UserAccountService;
import ru.shummi.service.UserService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;

@Service
public class UserAccountServiceImpl implements UserAccountService {
    private final SessionFactory sessionFactory;
    private final TransactionExecutorService<Object> transactionExecutorService;
    private final UserService userService;
    private final AccountService accountService;
    private final BigDecimal commission;
    private final BigDecimal amount;

    public UserAccountServiceImpl(
            SessionFactory sessionFactory,
            TransactionExecutorService<Object> transactionExecutorService,
            UserService userService,
            AccountService accountService,
            @Value("${account.commission}")
            String commission,
            @Value("${account.amount}")
            String amount
    ) {
        this.sessionFactory = sessionFactory;
        this.transactionExecutorService = transactionExecutorService;
        this.userService = userService;
        this.accountService = accountService;
        this.commission = new BigDecimal(commission.isBlank() ? "0.00" : commission);
        this.amount = new BigDecimal(amount.isBlank() ? "0.00" : amount);
    }

    public User registrationUser(String login) {
        final String finalLogin = login.trim().toLowerCase();
        return transactionExecutorService.executeInTransaction(() -> {
            Session session = sessionFactory.getCurrentSession();
            User user = userService.create(finalLogin);

            Account account = accountService.create(new Account(user, amount));
            session.detach(user);

            user.accounts().add(account);
            session.merge(user);

            return user;
        });
    }

    public Account addAccountToUserById(Long userId) {
        return transactionExecutorService.executeInTransaction(() -> {
            Session session = sessionFactory.getCurrentSession();
            User user = session.find(User.class, userId);
            return accountService.create(new Account(user, new BigDecimal("0.00")));
        });
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
    public void closeAccountById(Long accountId) {
        transactionExecutorService.executeInTransaction(() -> {
            Session session = sessionFactory.getCurrentSession();
            Account account = session.find(Account.class, accountId);
            session.remove(account);
            return account;
        });
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
    public void withdrawAccountById(Long accountId, BigDecimal withdraw) {
        verifyAmount(withdraw);
        transactionExecutorService.executeInTransaction((session) -> {
            Account account = session.find(Account.class, accountId);
            session.detach(account);
            account.withdrawMoney(withdraw);
            session.merge(account);
        });
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
    public void depositAccountById(Long accountId, BigDecimal deposit) {
        verifyAmount(deposit);
        transactionExecutorService.executeInTransaction((session) -> {
            Account account = session.find(Account.class, accountId);
            session.detach(account);
            account.depositMoney(deposit);
            session.merge(account);
        });
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
    public void transferAccountFromIdToId(
            Long sourceAccountId,
            Long targetAccountId,
            BigDecimal transfer
    ) {
        List<Long> ids = List.of(sourceAccountId, targetAccountId);
        verifyAmount(transfer);
        transactionExecutorService.executeInTransaction((session) -> {
            String selectAccounts = """
                    SELECT a FROM Account a
                    WHERE a.id IN :ids
                        OR EXISTS (
                            SELECT 1 FROM a.user u WHERE u.role = :role
                        )
                    """;
            Map<Long, Account> accounts = session
                    .createQuery(selectAccounts, Account.class)
                    .setParameter("id", ids)
                    .setParameter("role", UserRole.ADMIN_ROLE)
                    .getResultList()
                    .stream()
                    .collect(Collectors.toMap(Account::id, (a) -> a));

            RelatedAccounts rA = getRelatedAccounts(accounts, ids);

            session.detach(rA.sourceAccount());
            session.detach(rA.targetAccount());
            session.detach(rA.adminAccount());

            BigDecimal commissionFee = getCommissionFee(transfer, rA.sourceAccount(), rA.targetAccount());
            rA.sourceAccount().withdrawMoney(transfer.add(commissionFee));
            rA.targetAccount().depositMoney(transfer);
            rA.adminAccount().depositMoney(commissionFee);

            session.merge(rA.sourceAccount());
            session.merge(rA.targetAccount());
            session.merge(rA.adminAccount());
        });
    }

    private BigDecimal getCommissionFee(BigDecimal transfer, Account sourceAccount, Account targetAccount) {
        Long sourceUserId = sourceAccount.user().id();
        Long targetUserId = targetAccount.user().id();
        boolean equals = sourceUserId.equals(targetUserId);
        return (equals ? ZERO : commission).multiply(transfer);
    }

    private RelatedAccounts getRelatedAccounts(Map<Long, Account> accounts, List<Long> ids) {
        Account sourceAccount = accounts.get(ids.getFirst());
        Account targetAccount = accounts.get(ids.getLast());
        Long adminAccountId = accounts.keySet().stream()
                .filter(key -> !ids.contains(key))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No such admin account"));
        Account adminAccount = accounts.get(adminAccountId);
        accounts.clear();
        return new RelatedAccounts(sourceAccount, targetAccount, adminAccount);
    }

    private record RelatedAccounts(Account sourceAccount, Account targetAccount, Account adminAccount) {

    }

    private void verifyAmount(BigDecimal money) {
        if (money.compareTo(ZERO) <= 0) {
            throw new ApplicationException("Amount must be positive");
        }
    }
}
