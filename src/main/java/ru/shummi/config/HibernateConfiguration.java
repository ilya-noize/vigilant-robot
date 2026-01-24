package ru.shummi.config;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.shummi.entity.Account;
import ru.shummi.entity.User;

@Configuration
public class HibernateConfiguration {

    @Bean
    public SessionFactory sessionFactory() {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
        configuration
                .addAnnotatedClass(Account.class)
                .addAnnotatedClass(User.class)
                .addPackage("ru.shummi")
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/postgres")
                .setProperty("hibernate.connection.username", "postgres")
                .setProperty("hibernate.connection.password", "root")
                .setProperty("hibernate.show_sql", "true")
                .setProperty("hibernate.current_session_context_class", "thread");

        configuration
                // 2 first launch, debug, develop
                .setProperty("hibernate.hbm2ddl.auto", "create-drop");
//                .setProperty("hibernate.format_sql", "true")
//                .setProperty("hibernate.use_sql_comments", "true")

                // 2 production only
//                .setProperty("hibernate.hbm2ddl.auto", "update");

        return configuration.buildSessionFactory();
    }
}
