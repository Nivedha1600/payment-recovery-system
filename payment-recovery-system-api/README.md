# Payment Recovery System API

Spring Boot backend API for the Payment Recovery System.

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **PostgreSQL**
- **Lombok**
- **Redis** (for caching)
- **JWT** (for authentication)

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL 14+
- Redis (optional, for caching)

## Setup

1. **Create PostgreSQL Database**
   ```sql
   CREATE DATABASE payment_recovery_db;
   ```

2. **Configure Database Connection**
   - Update `application.properties` with your database credentials
   - Or set environment variables: `DB_USERNAME`, `DB_PASSWORD`

3. **Build the Project**
   ```bash
   mvn clean install
   ```

4. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```
   
   Or with a specific profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

## API Documentation

Once the application is running, access the API documentation at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Package Structure

```
com.paymentrecovery/
├── config/              # Configuration classes
├── controller/          # REST controllers
│   ├── api/            # Public API endpoints
│   └── automation/     # Automation API endpoints (for Python)
├── service/            # Business logic services
├── repository/         # Data access layer (JPA repositories)
├── model/             # Entity models
│   ├── entity/        # JPA entities
│   └── dto/           # Data Transfer Objects
├── security/          # Security configuration
│   ├── jwt/          # JWT utilities
│   └── auth/         # Authentication/Authorization
├── exception/         # Exception handling
├── util/             # Utility classes
└── PaymentRecoveryApplication.java
```

## Environment Variables

Key environment variables that can be set:

- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `JWT_SECRET` - JWT secret key (minimum 32 characters)
- `JWT_EXPIRATION` - JWT expiration time in milliseconds
- `REDIS_HOST` - Redis host
- `REDIS_PORT` - Redis port
- `AUTOMATION_API_KEY` - API key for Python automation services
- `CORS_ALLOWED_ORIGINS` - Allowed CORS origins

## Profiles

- **default** - Development configuration
- **dev** - Development profile with debug logging
- **prod** - Production profile with optimized settings

## Testing

Run tests with:
```bash
mvn test
```

## Building for Production

```bash
mvn clean package -Pprod
```

The JAR file will be created in `target/payment-recovery-system-api-1.0.0.jar`

Run the JAR:
```bash
java -jar target/payment-recovery-system-api-1.0.0.jar --spring.profiles.active=prod
```

