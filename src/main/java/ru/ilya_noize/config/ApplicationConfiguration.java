package ru.ilya_noize.config;

import org.springframework.context.annotation.*;

import java.util.Scanner;

@Configuration
@ComponentScan("ru.ilya_noize")
@PropertySource("classpath:/application.properties")
public class ApplicationConfiguration {

    @Bean
    public Scanner scanner() {
        return new Scanner(System.in);
    }
}
