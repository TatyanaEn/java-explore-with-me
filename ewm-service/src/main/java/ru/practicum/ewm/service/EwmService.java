package ru.practicum.ewm.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.practicum.ewm.service", "ru.practicum.ewm.stats.client"})
public class EwmService {

    public static void main(String[] args) {
        SpringApplication.run(EwmService.class);
    }

}
