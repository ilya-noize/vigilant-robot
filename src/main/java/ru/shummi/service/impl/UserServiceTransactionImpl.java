package ru.shummi.service.impl;

import org.springframework.stereotype.Service;
import ru.shummi.entity.User;
import ru.shummi.exception.ApplicationException;
import ru.shummi.service.TransactionExecutorService;
import ru.shummi.service.UserService;

import java.util.List;

@Service
public class UserServiceTransactionImpl implements UserService {
    private final TransactionExecutorService transactionExecutorService;

    public UserServiceTransactionImpl(TransactionExecutorService transactionExecutorService) {
        this.transactionExecutorService = transactionExecutorService;
    }

    @Override
    public User create(String login) {
        return transactionExecutorService.executeInTransaction(session -> {
            String jpql = "SELECT COUNT(u.id) FROM User u WHERE u.login = :login";
            Long resultCount = session.createQuery(jpql, Long.class)
                    .setParameter("login", login)
                    .getSingleResult();
            if (resultCount != 0)
                throw new ApplicationException("Is not unique login : %s"
                        .formatted(login));

            User user = new User(login);
            session.persist(user);

            return user;
        });
    }

    @Override
    public List<User> getAll() {
        return transactionExecutorService.executeInTransaction(session -> {
            String jpql = "SELECT u FROM User u LEFT JOIN FETCH u.accounts";

            return session.createQuery(jpql, User.class).getResultList();
        });
    }
}
