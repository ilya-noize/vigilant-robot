package ru.shummi.service.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;
import ru.shummi.entity.User;
import ru.shummi.service.TransactionExecutorService;
import ru.shummi.service.UserService;

import java.util.List;

@Service
public class UserServiceTransactionImpl implements UserService {
    private final SessionFactory sessionFactory;
    private final TransactionExecutorService<User> transactionExecutorService;

    public UserServiceTransactionImpl(
            SessionFactory sessionFactory,
            TransactionExecutorService<User> transactionExecutorService
    ) {
        this.sessionFactory = sessionFactory;
        this.transactionExecutorService = transactionExecutorService;
    }

    @Override
    public User create(String login) {
        return transactionExecutorService.executeInTransaction(() -> {
            Session session = sessionFactory.getCurrentSession();
            Transaction transaction = session.getTransaction();
            if (transaction == null) {
                session.beginTransaction();
            }
            User user = new User(login);
            session.persist(user);

            return user;
        });
    }

    @Override
    public List<User> getAll() {
        return transactionExecutorService.executeInTransaction(session -> {
            String jpql = """
                    SELECT u FROM User u LEFT JOIN FETCH u.accounts
                    """;
            return session.createQuery(jpql, User.class)
                    .getResultList();
        });
    }
}
