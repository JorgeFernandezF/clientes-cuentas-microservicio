package com.cuentabancaria.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.cuentabancaria")
public class CuentasBancariasApplication {
    public static void main(String[] args) {
        SpringApplication.run(CuentasBancariasApplication.class, args);
    }
}