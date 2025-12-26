# Payment Recovery System

A production-grade B2B Payment Recovery system built with Java Spring Boot, Python automation services, Angular frontend, and PostgreSQL database.

## Architecture Overview

This system follows a **separation of concerns** architecture where:
- **Java Spring Boot** is the system of record with exclusive database access
- **Python** handles automation (scheduling, reminders, extraction) via REST API
- **Angular** provides the user interface
- **PostgreSQL** stores all data with ACID compliance

### Key Design Principles

1. **Single Source of Truth:** Java is the only system with database access
2. **Stateless Services:** All services are stateless for horizontal scaling
3. **REST API Integration:** Java and Python communicate via REST API
4. **Security First:** Multiple layers of security and authentication
5. **Scalable:** Designed for horizontal scaling from day one

## System Components

### Backend (Java Spring Boot)
- **Location:** `payment-recovery-system-api/`
- **Responsibilities:**
  - System of record (all database operations)
  - RESTful API for frontend
  - RESTful API for Python services
  - Business logic and validation
  - Authentication and authorization
  - Audit logging

### Automation Services (Python)
- **Location:** `payment-recovery-system-automation/` (to be created)
- **Responsibilities:**
  - Task scheduling
  - Reminder generation and dispatch
  - Document extraction
  - Email/SMS processing
  - **No direct database access** - all operations via Java API

### Frontend (Angular)
- **Location:** `payment-recovery-system-ui/`
- **Responsibilities:**
  - User interface
  - Dashboard and analytics
  - Case management
  - Document management
  - Real-time notifications

### Database (PostgreSQL)
- **Responsibilities:**
  - Primary data store
  - ACID compliance
  - Transaction management
  - Audit trail

## Documentation

- **[Architecture Documentation](docs/ARCHITECTURE.md)** - Complete system architecture
- **[Design Decisions](docs/DESIGN_DECISIONS.md)** - Rationale behind design choices
- **[Integration Guide](docs/INTEGRATION_GUIDE.md)** - Java-Python integration details

## Quick Start

### Prerequisites
- Java 17+
- Python 3.11+
- Node.js 18+
- PostgreSQL 14+
- Maven 3.8+
- Docker (optional)

### Development Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd PaymentRecoverySystem
   ```

2. **Set up PostgreSQL**
   ```bash
   # Create database
   createdb payment_recovery_db
   ```

3. **Configure Java Backend**
   ```bash
   cd payment-recovery-system-api
   # Configure application.properties with database credentials
   mvn clean install
   mvn spring-boot:run
   ```

4. **Configure Python Services**
   ```bash
   cd payment-recovery-system-automation
   python -m venv venv
   source venv/bin/activate  # On Windows: venv\Scripts\activate
   pip install -r requirements.txt
   # Configure environment variables
   python scheduler_service.py
   ```

5. **Configure Angular Frontend**
   ```bash
   cd payment-recovery-system-ui
   npm install
   npm start
   ```

## API Endpoints

### Frontend API (Java)
- Base URL: `http://localhost:8080/api/v1`
- Authentication: JWT tokens

### Automation API (Java → Python)
- Base URL: `http://localhost:8080/api/v1/automation`
- Authentication: API keys or OAuth2 client credentials

See [Integration Guide](docs/INTEGRATION_GUIDE.md) for detailed API documentation.

## Security

- JWT authentication for frontend
- API key authentication for service-to-service
- TLS/HTTPS for all communications
- Encryption at rest for sensitive data
- Role-based access control (RBAC)
- Comprehensive audit logging

## Scalability

- **Horizontal Scaling:** All services are stateless
- **Caching:** Redis for session and data caching
- **Load Balancing:** Supports multiple instances
- **Database:** Read replicas for read-heavy operations
- **Async Processing:** Non-blocking operations for better performance

## Monitoring

- Health checks for all services
- Metrics collection (Prometheus)
- Log aggregation (ELK Stack)
- Distributed tracing
- Alerting on anomalies

## Testing

```bash
# Java tests
cd payment-recovery-system-api
mvn test

# Python tests
cd payment-recovery-system-automation
pytest

# Angular tests
cd payment-recovery-system-ui
npm test
```

## Deployment

The system is designed for containerized deployment:
- Docker containers for all services
- Kubernetes for orchestration
- CI/CD pipeline for automated deployments

## Contributing

1. Follow the coding standards defined in the project
2. Write tests for new features
3. Update documentation as needed
4. Follow the architecture principles

## License

[Your License Here]

## Support

For questions or issues, please contact the development team or create an issue in the repository.

