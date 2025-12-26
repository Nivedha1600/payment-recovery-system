package com.paymentrecovery.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI paymentRecoveryOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Payment Recovery System API")
                .description("RESTful API for B2B Payment Recovery System")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Payment Recovery Team")
                    .email("support@paymentrecovery.com"))
                .license(new License()
                    .name("Proprietary")
                    .url("https://paymentrecovery.com")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Development Server"),
                new Server()
                    .url("https://api.paymentrecovery.com")
                    .description("Production Server")
            ));
    }
}

