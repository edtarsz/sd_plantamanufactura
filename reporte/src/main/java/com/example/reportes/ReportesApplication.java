package com.example.reportes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.reportes", "com.example.defectos"})
@EntityScan(basePackages = {"com.example.reportes", "com.example.defectos"})
@EnableJpaRepositories(basePackages = {"com.example.reportes", "com.example.defectos"})
public class ReportesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportesApplication.class, args);
    }

}
