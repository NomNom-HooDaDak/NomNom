package com.p1.nomnom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = "com.p1.nomnom")
@EnableJpaAuditing // JPA Auditing 기능 활성화
public class NomNomApplication {

    public static void main(String[] args) {
        SpringApplication.run(NomNomApplication.class, args);
    }

}


