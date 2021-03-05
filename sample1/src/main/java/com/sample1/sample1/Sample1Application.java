package com.sample1.sample1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = { "com.sample1.sample1" })
public class Sample1Application {

    public static void main(String[] args) {
        SpringApplication.run(Sample1Application.class, args);
    }
}
