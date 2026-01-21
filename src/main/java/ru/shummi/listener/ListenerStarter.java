package ru.shummi.listener;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListenerStarter {
    private final ConsoleListener consoleListener;
    private final SessionFactory sessionFactory;
    private Thread listener;

    @Autowired
    public ListenerStarter(ConsoleListener consoleListener, SessionFactory sessionFactory) {
        this.consoleListener = consoleListener;
        this.sessionFactory = sessionFactory;
    }

    @PostConstruct
    public void init() {
        sessionFactory.openSession();
        this.listener = new Thread(consoleListener::update);
        this.listener.start();
    }

    @PreDestroy
    public void destroy() {
        listener.interrupt();
        sessionFactory.getCurrentSession().close();
    }
}
