package ru.ilya_noize;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    private static final AnnotationConfigApplicationContext context =
            new AnnotationConfigApplicationContext("ru.ilya_noize");

    public Application() {

    }

    public static void main(String[] args) {
        new Application();
    }

    public static void close() {context.close();}
}
