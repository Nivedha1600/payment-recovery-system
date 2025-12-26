package com.paymentrecovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for Payment Recovery System API
 * 
 * @SpringBootApplication - Enables auto-configuration and component scanning
 * @EnableJpaAuditing - Enables JPA auditing (created/updated timestamps)
 * @EnableCaching - Enables Spring Cache abstraction
 * @EnableAsync - Enables async method execution
 * @EnableScheduling - Enables scheduled task execution
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableScheduling
public class PaymentRecoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentRecoveryApplication.class, args);
    }
}

