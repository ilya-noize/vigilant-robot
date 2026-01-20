package ru.shummi.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;
import ru.shummi.model.User;

import java.math.BigDecimal;

@Service
public class UserAccountServiceImpl implements UserAccountService{
    private final SessionFactory sessionFactory;

    public UserAccountServiceImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User registrationUser(String login) {
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();



            transaction.commit();
        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
                throw e;
            }
        }
    }

    @Override
    public int addAccountToUserById(int userId) {
        return 0;
    }

    @Override
    public void closeAccountById(int accountId) {

    }

    @Override
    public void withdrawAccountById(int accountId, BigDecimal withdraw) {

    }

    @Override
    public void depositAccountById(int accountId, BigDecimal deposit) {

    }

    @Override
    public void transferAccountFromIdToId(int sourceAccountId, int targetAccountId, BigDecimal transfer) {

    }
}
