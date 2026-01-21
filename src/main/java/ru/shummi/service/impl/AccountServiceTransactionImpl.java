package ru.shummi.service.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import ru.shummi.entity.Account;
import ru.shummi.service.AccountService;
import ru.shummi.service.TransactionExecutorService;

@Service
public class AccountServiceTransactionImpl implements AccountService {
    private final SessionFactory sessionFactory;
    private final TransactionExecutorService<Account> transactionExecutorService;

    public AccountServiceTransactionImpl(SessionFactory sessionFactory, TransactionExecutorService<Account> transactionExecutorService) {
        this.sessionFactory = sessionFactory;
        this.transactionExecutorService = transactionExecutorService;
    }

    @Override
    public Account create(Account account) {
        return transactionExecutorService.executeInTransaction(() -> {
            Session session = sessionFactory.getCurrentSession();
            session.persist(account);
            return account;
        });
    }
}
