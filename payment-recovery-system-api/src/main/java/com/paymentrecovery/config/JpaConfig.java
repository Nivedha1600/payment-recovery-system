package com.paymentrecovery.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA configuration
 * Enables JPA repositories and transaction management
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.paymentrecovery.repository")
@EnableTransactionManagement
public class JpaConfig {
    // JPA configuration is handled by Spring Boot auto-configuration
    // Additional custom configurations can be added here if needed
}

