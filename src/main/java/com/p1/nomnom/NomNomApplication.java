package com.p1.nomnom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = "com.p1.nomnom")
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class NomNomApplication {

    public static void main(String[] args) {
        SpringApplication.run(NomNomApplication.class, args);
    }

}


