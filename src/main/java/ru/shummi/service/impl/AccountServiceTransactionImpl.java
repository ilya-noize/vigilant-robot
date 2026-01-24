package ru.shummi.service.impl;

import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.stereotype.Service;
import ru.shummi.entity.Account;
import ru.shummi.service.AccountService;
import ru.shummi.service.TransactionExecutorService;

@Service
public class AccountServiceTransactionImpl implements AccountService {
    private final TransactionExecutorService transactionExecutorService;

    public AccountServiceTransactionImpl(
            TransactionExecutorService transactionExecutorService
    ) {
        this.transactionExecutorService = transactionExecutorService;
    }

    @Override
    public Account create(Account account) {
        return transactionExecutorService.executeInTransaction((session) -> {
            if (!session.getTransaction().getStatus().equals(TransactionStatus.ACTIVE)) {
                return null;
            }
            session.persist(account);
            return account;
        });
    }
}
