package com.officemind.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.officemind")
@EntityScan(basePackages = "com.officemind.infrastructure")
@EnableJpaRepositories(basePackages = "com.officemind.infrastructure")
public class OfficeMindApplication {

    public static void main(String[] args) {
        SpringApplication.run(OfficeMindApplication.class, args);
    }
}
