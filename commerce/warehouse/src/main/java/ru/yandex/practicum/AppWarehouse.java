package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AppWarehouse {
    public static void main(String[] args) {
        SpringApplication.run(AppWarehouse.class, args);
    }
}
