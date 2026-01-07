package ru.ilya_noize.listener;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class ListenerStarter {
    private final ConsoleListener consoleListener;
    private Thread listener;

    public ListenerStarter(ConsoleListener consoleListener) {
        this.consoleListener = consoleListener;
    }

    @PostConstruct
    public void init() {
        this.listener.start();
        this.listener = new Thread(() -> {
            consoleListener.construct();
            consoleListener.update();
        });
    }

    @PreDestroy
    public void destroy() {
        listener.interrupt();
        consoleListener.destroy();
    }
}
