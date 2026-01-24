package ru.shummi.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class TransactionExecutorService {
    private final SessionFactory sessionFactory;

    public TransactionExecutorService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Получаем текущую сессию.
     * Получаем транзакцию из сессии.
     * --
     * Если транзакция в статусе АКТИВНЫЙ, то выполняем команды в сессии (get|find) и выводим результат -> конец.
     * --
     * В другом случае начинаем транзакцию, получаем результат выполнения действия и делаем коммит транзакции
     * В случае ошибки делаем откат изменений и выбрасываем исключение
     * В конце закрываем сессию.
     * --
     * Итог: сессия активна всегда для получения данных, но закрывается если делаем транзакцию в ней.
     */
    public <T> T executeInTransaction(Function<Session, T> action) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();

        if (!transaction.getStatus().equals(TransactionStatus.NOT_ACTIVE)) {
            return action.apply(session);
        }
        try {
            session.beginTransaction();
            T value = action.apply(session);
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
