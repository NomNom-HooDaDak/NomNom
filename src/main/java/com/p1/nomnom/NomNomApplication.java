package com.p1.nomnom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = "com.p1.nomnom")
@EnableJpaRepositories(basePackages = "com.p1.nomnom")
@SpringBootApplication
@EnableJpaAuditing
public class NomNomApplication {
    public static void main(String[] args) {
        SpringApplication.run(NomNomApplication.class, args);
    }
}
