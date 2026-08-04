package com.officemind.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.officemind")
public class OfficeMindApplication {

    public static void main(String[] args) {
        SpringApplication.run(OfficeMindApplication.class, args);
    }
}