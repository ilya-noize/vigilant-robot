package ru.shummi;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {

    public Application() {

    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("ru.shummi");
    }
}
