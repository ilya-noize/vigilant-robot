package ru.shummi.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class TransactionExecutorService<T> {
    private final SessionFactory sessionFactory;

    public TransactionExecutorService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T executeInTransaction(Supplier<T> action) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();

        if (!transaction.getStatus().equals(TransactionStatus.NOT_ACTIVE)) {
            return action.get();
        }
        try {
            session.beginTransaction();
            T value = action.get();
            transaction.commit();
            return value;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void executeInTransaction(Consumer<Session> action) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();

        if (!transaction.getStatus().equals(TransactionStatus.NOT_ACTIVE)) {
            action.accept(session);
        }
        try {
            transaction = session.beginTransaction();
            action.accept(session);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
