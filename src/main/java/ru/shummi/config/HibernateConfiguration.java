package ru.shummi.config;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import ru.shummi.entity.Account;
import ru.shummi.entity.User;

import java.io.IOException;

@Configuration
public class HibernateConfiguration {

    @Bean
    public SessionFactory sessionFactory() throws IOException {

        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
        configuration
                .addAnnotatedClass(Account.class)
                .addAnnotatedClass(User.class)
                .addPackage("ru.shummi")
                .setProperties(PropertiesLoaderUtils
                        .loadProperties(new ClassPathResource("/application.properties")));

        return configuration.buildSessionFactory();
    }
}
