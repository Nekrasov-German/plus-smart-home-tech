package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CollectorApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CollectorApp.class, args);
    }
}
