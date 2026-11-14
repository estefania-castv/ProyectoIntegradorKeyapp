package com.Cesde.keyapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling   // permite que SCierreReservas libere las aulas cada minuto
public class KeyappApplication {

    public static void main(String[] args) {
        SpringApplication.run(KeyappApplication.class, args);
    }

}
