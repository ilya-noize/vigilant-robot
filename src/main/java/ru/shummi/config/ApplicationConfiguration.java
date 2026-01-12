package ru.shummi.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("ru.shummi")
@PropertySource("classpath:/application.properties")
public class ApplicationConfiguration {
}
