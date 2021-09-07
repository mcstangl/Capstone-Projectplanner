package de.mcstangl.projectplanner.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan(basePackages = {"de.mcstangl.projectplanner.model"})
@EnableJpaRepositories(basePackages="de.mcstangl.projectplanner.repository")
@EnableTransactionManagement
public class JpaConfig {
}
