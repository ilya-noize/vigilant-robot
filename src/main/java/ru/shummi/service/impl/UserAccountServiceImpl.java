package ru.shummi.service.impl;

import org.hibernate.Session;
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

import static java.math.BigDecimal.ZERO;

@Service
public class UserAccountServiceImpl implements UserAccountService {
    private final TransactionExecutorService transactionExecutorService;
    private final UserService userService;
    private final AccountService accountService;
    private final BigDecimal commission;
    private final BigDecimal amount;
    private final Long adminId;
    private final Long adminAccountId;

    public UserAccountServiceImpl(
            TransactionExecutorService transactionExecutorService,
            UserService userService,
            AccountService accountService,
            @Value("${account.commission}")
            String commission,
            @Value("${account.amount}")
            String amount
    ) {
        this.transactionExecutorService = transactionExecutorService;
        this.userService = userService;
        this.accountService = accountService;
        this.commission = new BigDecimal(commission.isBlank() ? "0.00" : commission);
        this.amount = new BigDecimal(amount.isBlank() ? "0.00" : amount);

        long[] adminResourcesIds = findAdminResourcesIds();
        this.adminId = adminResourcesIds[0];
        this.adminAccountId = adminResourcesIds[1];
    }

    private long[] findAdminResourcesIds() {
        return this.transactionExecutorService.executeInTransaction(session -> {
            try {
                String selectAdminResources = "SELECT u, a FROM User u JOIN u.accounts a " +
                        "WHERE u.login = :login OR u.role = :role";
                List<Object[]> resources = session.createQuery(selectAdminResources, Object[].class)
                        .setParameter("login", "admin")
                        .setParameter("role", UserRole.ADMIN_ROLE)
                        .getResultList();
                long[] result = new long[2];
                if(!resources.isEmpty()) {
                    for (Object[] row : resources) {
                        User admin = (User) row[0];
                        result[0] = admin.id();
                        Account adminAccount = (Account) row[1];
                        result[1] = adminAccount.id();
                    }
                } else {
                    User admin = new User("admin");
                    admin.setRole(UserRole.ADMIN_ROLE.name());
                    session.persist(admin);
                    Account adminAccount = new Account(admin, ZERO);
                    session.persist(adminAccount);

                    result[0] = admin.id();
                    result[1] = adminAccount.id();
                }
                return result;
            } catch (Exception e) {
                throw e;
            }
        });
    }

    public User registrationUser(String login) {
        final String finalLogin = login.trim().toLowerCase();
        return transactionExecutorService.executeInTransaction((session) -> {
            User user = userService.create(finalLogin);
            Account account = accountService.create(new Account(user, amount));
            user.accounts().add(account);
            session.merge(user);

            return user;
        });
    }

    public Account addAccountToUserById(Long userId) {
        isNotSystemResources(userId);
        return transactionExecutorService.executeInTransaction((session) -> {
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
        isNotSystemResources(accountId);
        transactionExecutorService.executeInTransaction(session -> {
            // select account and user
            AccountResources accountResources = findAccountResources(accountId, session);
            // select user's accounts
            List<Account> accounts = accountResources.allAccountByUser();
            Account account = accountResources.account();
            BigDecimal money = account.money();
            if (!money.equals(ZERO)) {
                Account firstAccount = accounts.stream()
                        .filter(acc -> !acc.id().equals(accountId))
                        .findFirst().orElseThrow(() -> new ApplicationException(
                                "Actions with this single account are denied (AccountId:%s)"
                                        .formatted(accountId)));
                // update account money
                transferBetweenAccountsOwner(account, firstAccount, money);
            }
            // delete account
            session.remove(account);
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
        isPositiveAmount(withdraw);
        transactionExecutorService.executeInTransaction(session -> {
            Account account = session.find(Account.class, accountId);
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
        isPositiveAmount(deposit);
        isNotSystemResources(accountId);
        transactionExecutorService.executeInTransaction((session) -> {
            Account account = session.find(Account.class, accountId);
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
        isPositiveAmount(transfer);
        isNotSystemResources(sourceAccountId);
        transactionExecutorService.executeInTransaction((session) -> {

            // select admin account
            Account adminAccount = session.find(Account.class, adminAccountId);
            Account targetAccount;
            Long targetUserId;
            // if transfer to admin then target* equals admin*
            if (targetAccountId.equals(adminAccountId)) {
                targetAccount = adminAccount;
                targetUserId = adminId;
            } else {
                // select account + user (target)
                AccountResources targetResources = findAccountResources(targetAccountId, session);
                targetAccount = targetResources.account;
                targetUserId = targetResources.user.id();
            }
            //select account + user (source)
            AccountResources sourceResources = findAccountResources(sourceAccountId, session);
            Long sourceUserId = sourceResources.user.id();
            Account sourceAccount = sourceResources.account;
            if (!sourceUserId.equals(targetUserId)) {
                transferBetweenAccountWithCommission(sourceAccount, targetAccount, adminAccount, transfer);
            } else {
                transferBetweenAccountsOwner(sourceAccount, targetAccount, transfer);
            }
        });
    }

    private AccountResources findAccountResources(Long accountId, Session session) {
        String selectUserAccountResourcesByAccountId = """
                SELECT u, a
                 FROM User u
                 JOIN u.accounts a
                 WHERE a.id = :accountId
                """;
        List<Object[]> sourceResources = session
                .createQuery(selectUserAccountResourcesByAccountId, Object[].class)
                .setParameter("accountId", accountId)
                .getResultList();
        User sourceUser = null;
        Account sourceAccount = null;
        for (Object[] row : sourceResources) {
            sourceUser = (User) row[0];
            sourceAccount = (Account) row[1];
        }
        return new AccountResources(sourceAccount, sourceUser);
    }

    private record AccountResources(Account account, User user) {
        public List<Account> allAccountByUser() {
            return user.accounts();
        }
    }

    private void transferBetweenAccountWithCommission(
            Account sourceAccount,
            Account targetAccount,
            Account adminAccount,
            BigDecimal transfer
    ) {
        BigDecimal commissionFee = commission.multiply(transfer);
        assert sourceAccount != null;
        sourceAccount.withdrawMoney(transfer.add(commissionFee));
        assert targetAccount != null;
        targetAccount.depositMoney(transfer);
        adminAccount.depositMoney(commissionFee);
    }

    private void transferBetweenAccountsOwner(
            Account sourceAccount,
            Account targetAccount,
            BigDecimal transfer
    ) {
        isPositiveAmount(transfer);
        sourceAccount.withdrawMoney(transfer);
        targetAccount.depositMoney(transfer);
    }

    private void isNotSystemResources(Long id) {
        if (id.equals(adminId))
            throw new ApplicationException("Actions with this resource are denied (AccountId:%s)"
                    .formatted(id));
    }

    private void isPositiveAmount(BigDecimal money) {
        if (money.compareTo(ZERO) <= 0) {
            throw new ApplicationException("Amount must be positive");
        }
    }
}
