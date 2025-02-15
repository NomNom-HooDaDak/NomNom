package com.p1.nomnom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)

public class NomNomApplication {

    public static void main(String[] args) {
        SpringApplication.run(NomNomApplication.class, args);
    }

}
