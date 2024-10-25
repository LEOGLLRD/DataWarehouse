package com.stockchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.HashMap;

@SpringBootApplication
@EnableMongoRepositories(basePackages = {"com.stockchain.repository"})
public class SpringSecurityJwtApplication {


    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityJwtApplication.class, args);
    }
    
}
