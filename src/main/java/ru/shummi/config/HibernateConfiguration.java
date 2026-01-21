package ru.shummi.config;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.shummi.entity.Account;
import ru.shummi.entity.User;

@Configuration
public class HibernateConfiguration {

    @Bean
    public SessionFactory sessionFactory(){
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
        configuration
                .addAnnotatedClass(Account.class)
                .addAnnotatedClass(User.class)
                .addPackage("ru.shummi")
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/postgres")
                .setProperty("hibernate.connection.user", "postgres")
                .setProperty("hibernate.connection.password", "root")
                .setProperty("hibernate.show_sql", "true")
                .setProperty("hibernate.hdm2dll.auto", "create-drop")
                .setProperty("hibernate.current_session_context_class", "thread");

        return configuration.buildSessionFactory();
    }
}
