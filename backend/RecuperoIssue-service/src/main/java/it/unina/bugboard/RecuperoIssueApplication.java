package it.unina.bugboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "it.unina.bugboard.entity")
@EnableJpaRepositories(basePackages = "it.unina.bugboard.repository")
public class RecuperoIssueApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecuperoIssueApplication.class, args);
    }
}
