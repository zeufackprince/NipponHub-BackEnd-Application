package com.nipponhub.nipponhubv0.Configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.nipponhub.nipponhubv0.Repositories.mysql"  
)
@EntityScan(
    basePackages = "com.nipponhub.nipponhubv0.Models"             
)
public class MySQLConfig {
    // Spring Boot auto-configures the DataSource and EntityManagerFactory
    // from application.properties — no manual bean declaration needed here.
}