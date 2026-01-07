package ru.ilya_noize;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    private final AnnotationConfigApplicationContext context =
            new AnnotationConfigApplicationContext("ru.ilya_noize");

    public Application() {}

    public void shutdown() {context.close();}

    public static void main(String[] args) {
        new Application();
    }
}
