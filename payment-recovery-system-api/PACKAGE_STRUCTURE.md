# Recommended Package Structure for SaaS Product

This document outlines the recommended package structure for a production-grade SaaS application built with Spring Boot.

## Complete Package Structure

```
com.paymentrecovery/
│
├── PaymentRecoveryApplication.java          # Main application class
│
├── config/                                   # Configuration classes
│   ├── SecurityConfig.java                  # Security configuration
│   ├── JpaConfig.java                       # JPA configuration
│   ├── RedisConfig.java                     # Redis cache configuration
│   ├── OpenApiConfig.java                   # Swagger/OpenAPI configuration
│   ├── WebConfig.java                       # Web MVC configuration
│   └── AsyncConfig.java                     # Async processing configuration
│
├── controller/                              # REST Controllers
│   ├── api/                                 # Public API endpoints
│   │   ├── AuthController.java              # Authentication endpoints
│   │   ├── RecoveryCaseController.java      # Recovery case management
│   │   ├── PaymentController.java           # Payment operations
│   │   ├── CustomerController.java          # Customer management
│   │   ├── DocumentController.java          # Document operations
│   │   └── NotificationController.java      # Notification management
│   │
│   └── automation/                         # Automation API (for Python services)
│       ├── AutomationTaskController.java    # Task scheduling endpoints
│       ├── AutomationCaseController.java    # Case retrieval endpoints
│       ├── AutomationExtractionController.java  # Extraction endpoints
│       └── AutomationReminderController.java    # Reminder endpoints
│
├── service/                                 # Business Logic Layer
│   ├── RecoveryCaseService.java             # Recovery case business logic
│   ├── PaymentService.java                  # Payment processing logic
│   ├── CustomerService.java                 # Customer management logic
│   ├── DocumentService.java                 # Document handling logic
│   ├── NotificationService.java             # Notification orchestration
│   ├── AuditService.java                    # Audit trail management
│   ├── AutomationIntegrationService.java    # Integration with Python services
│   └── impl/                                # Service implementations (if using interfaces)
│
├── repository/                              # Data Access Layer (JPA Repositories)
│   ├── RecoveryCaseRepository.java          # Recovery case data access
│   ├── PaymentRepository.java               # Payment data access
│   ├── CustomerRepository.java              # Customer data access
│   ├── DocumentRepository.java               # Document metadata access
│   ├── NotificationRepository.java           # Notification data access
│   ├── AuditLogRepository.java              # Audit log access
│   └── ScheduledTaskRepository.java         # Scheduled task access
│
├── model/                                   # Data Models
│   ├── entity/                              # JPA Entities (Database tables)
│   │   ├── BaseEntity.java                  # Base entity with common fields
│   │   ├── RecoveryCase.java                # Recovery case entity
│   │   ├── Payment.java                     # Payment entity
│   │   ├── Customer.java                    # Customer entity
│   │   ├── Document.java                    # Document entity
│   │   ├── Notification.java                # Notification entity
│   │   ├── AuditLog.java                    # Audit log entity
│   │   ├── ScheduledTask.java               # Scheduled task entity
│   │   └── User.java                        # User entity
│   │
│   ├── dto/                                 # Data Transfer Objects
│   │   ├── request/                         # Request DTOs
│   │   │   ├── CreateRecoveryCaseRequest.java
│   │   │   ├── UpdateRecoveryCaseRequest.java
│   │   │   ├── CreatePaymentRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   └── CreateCustomerRequest.java
│   │   │
│   │   ├── response/                        # Response DTOs
│   │   │   ├── RecoveryCaseResponse.java
│   │   │   ├── PaymentResponse.java
│   │   │   ├── CustomerResponse.java
│   │   │   ├── AuthResponse.java
│   │   │   └── ApiResponse.java            # Generic API response wrapper
│   │   │
│   │   └── automation/                      # Automation API DTOs
│   │       ├── AutomationTaskRequest.java
│   │       ├── ExtractionRequest.java
│   │       └── ReminderRequest.java
│   │
│   └── enums/                               # Enumerations
│       ├── RecoveryCaseStatus.java
│       ├── PaymentStatus.java
│       ├── NotificationType.java
│       ├── TaskType.java
│       └── UserRole.java
│
├── security/                                # Security Components
│   ├── jwt/                                 # JWT utilities
│   │   ├── JwtTokenProvider.java            # JWT token generation/validation
│   │   └── JwtAuthenticationFilter.java     # JWT authentication filter
│   │
│   ├── auth/                                # Authentication/Authorization
│   │   ├── AuthenticationService.java       # Authentication logic
│   │   ├── UserDetailsServiceImpl.java      # User details service
│   │   └── SecurityUser.java                # Security user wrapper
│   │
│   └── exception/                           # Security exceptions
│       └── JwtAuthenticationException.java
│
├── exception/                               # Exception Handling
│   ├── GlobalExceptionHandler.java          # Global exception handler
│   ├── ResourceNotFoundException.java       # Custom exceptions
│   ├── ValidationException.java
│   ├── BusinessException.java
│   └── ErrorResponse.java                   # Error response DTO
│
├── util/                                    # Utility Classes
│   ├── DateUtil.java                        # Date/time utilities
│   ├── ValidationUtil.java                  # Validation utilities
│   ├── PaginationUtil.java                  # Pagination utilities
│   ├── FileUtil.java                        # File handling utilities
│   └── Constants.java                       # Application constants
│
├── mapper/                                  # Object Mappers (MapStruct or manual)
│   ├── RecoveryCaseMapper.java
│   ├── PaymentMapper.java
│   ├── CustomerMapper.java
│   └── DocumentMapper.java
│
├── validator/                               # Custom Validators
│   ├── UniqueEmailValidator.java
│   ├── ValidCurrencyValidator.java
│   └── FutureDateValidator.java
│
└── aspect/                                  # AOP Aspects (optional)
    ├── LoggingAspect.java                   # Logging aspect
    ├── PerformanceAspect.java               # Performance monitoring
    └── AuditAspect.java                     # Audit aspect
```

## Package Organization Principles

### 1. **Layered Architecture**
- **Controller Layer**: Handles HTTP requests/responses
- **Service Layer**: Contains business logic
- **Repository Layer**: Data access abstraction
- **Model Layer**: Data structures (entities, DTOs)

### 2. **Separation by Feature vs. Layer**
This structure uses a **hybrid approach**:
- **Layer-based** for core structure (controller, service, repository)
- **Feature-based** sub-packages where it makes sense (api vs automation)

### 3. **Naming Conventions**
- **Controllers**: `*Controller.java`
- **Services**: `*Service.java`
- **Repositories**: `*Repository.java`
- **Entities**: Singular noun (e.g., `RecoveryCase`, not `RecoveryCases`)
- **DTOs**: Descriptive names with Request/Response suffix
- **Enums**: Descriptive names ending with type (e.g., `RecoveryCaseStatus`)

### 4. **Package Responsibilities**

#### `config/`
- Spring configuration classes
- Bean definitions
- Security configuration
- External service configurations

#### `controller/`
- REST API endpoints
- Request/response handling
- Input validation
- HTTP status code management
- **No business logic** - delegates to services

#### `service/`
- Business logic implementation
- Transaction management
- Orchestration of multiple repositories
- Business rule enforcement
- **No direct HTTP concerns**

#### `repository/`
- Data access layer
- JPA repository interfaces
- Custom query methods
- **No business logic**

#### `model/entity/`
- JPA entities
- Database table mappings
- Relationships (OneToMany, ManyToOne, etc.)
- JPA annotations

#### `model/dto/`
- Data Transfer Objects
- Request/Response models
- Validation annotations
- **No JPA annotations**

#### `security/`
- Authentication logic
- Authorization rules
- JWT handling
- Security filters

#### `exception/`
- Custom exceptions
- Global exception handling
- Error response formatting

#### `util/`
- Reusable utility methods
- Static helper methods
- Common functionality

## Best Practices

### 1. **Single Responsibility Principle**
Each class should have one clear responsibility:
- Controllers handle HTTP
- Services handle business logic
- Repositories handle data access

### 2. **Dependency Direction**
```
Controller → Service → Repository → Entity
```
- Controllers depend on Services
- Services depend on Repositories
- Repositories depend on Entities
- **Never reverse this direction**

### 3. **Package Visibility**
- Use package-private access when appropriate
- Public interfaces for external access
- Internal implementation details should be protected

### 4. **DTOs vs Entities**
- **Entities**: Used internally, contain JPA annotations
- **DTOs**: Used for API communication, no JPA annotations
- **Never expose entities directly** in API responses

### 5. **Exception Handling**
- Custom exceptions in `exception/` package
- Global exception handler in `controller/` or `exception/`
- Consistent error response format

### 6. **Configuration Management**
- Environment-specific properties
- Use `@ConfigurationProperties` for type-safe configuration
- Externalize sensitive data (secrets, API keys)

## Example: Feature-Based Alternative Structure

For very large applications, you might consider a feature-based structure:

```
com.paymentrecovery/
├── feature/
│   ├── recoverycase/
│   │   ├── RecoveryCaseController.java
│   │   ├── RecoveryCaseService.java
│   │   ├── RecoveryCaseRepository.java
│   │   └── RecoveryCase.java
│   │
│   ├── payment/
│   │   ├── PaymentController.java
│   │   ├── PaymentService.java
│   │   ├── PaymentRepository.java
│   │   └── Payment.java
│   │
│   └── customer/
│       ├── CustomerController.java
│       ├── CustomerService.java
│       ├── CustomerRepository.java
│       └── Customer.java
│
└── shared/
    ├── config/
    ├── security/
    ├── exception/
    └── util/
```

**Note**: The layer-based structure (recommended above) is generally better for SaaS applications as it:
- Makes it easier to find code by layer
- Enforces clear separation of concerns
- Is more familiar to Spring Boot developers
- Scales well for medium to large applications

## Directory Structure in File System

```
payment-recovery-system-api/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── paymentrecovery/
│   │   │           └── [package structure as above]
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── db/
│   │           └── migration/              # Flyway migrations (if used)
│   │
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── paymentrecovery/
│       │           └── [mirror main structure]
│       │
│       └── resources/
│           └── application-test.properties
│
├── pom.xml
├── README.md
└── .gitignore
```

## Summary

This package structure provides:
- ✅ Clear separation of concerns
- ✅ Easy navigation and maintenance
- ✅ Scalability for growth
- ✅ Alignment with Spring Boot best practices
- ✅ Support for multi-tenant SaaS architecture
- ✅ Clear boundaries for team collaboration

