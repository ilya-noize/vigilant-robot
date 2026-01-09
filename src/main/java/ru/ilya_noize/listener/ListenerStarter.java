package ru.ilya_noize.listener;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListenerStarter {
    private final ConsoleListener consoleListener;
    private Thread listener;

    @Autowired
    public ListenerStarter(ConsoleListener consoleListener) {
        this.consoleListener = consoleListener;
    }

    @PostConstruct
    public void init() {
        this.listener = new Thread(consoleListener::update);
        this.listener.start();
    }

    @PreDestroy
    public void destroy() {
        listener.interrupt();
    }
}
