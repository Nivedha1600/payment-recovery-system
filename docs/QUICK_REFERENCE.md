# Payment Recovery System - Quick Reference

## Architecture Summary

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│   Angular   │ ──────> │   Java      │ ──────> │ PostgreSQL  │
│  Frontend   │  REST   │  Backend    │  JPA    │  Database   │
└─────────────┘         └──────┬──────┘         └─────────────┘
                               │
                               │ REST API
                               │
                      ┌────────▼────────┐
                      │  Python         │
                      │  Automation     │
                      │  (No DB Access) │
                      └─────────────────┘
```

## Responsibility Matrix

| Component | Database Access | Business Logic | Automation | UI |
|-----------|----------------|----------------|------------|-----|
| **Java** | ✅ Exclusive | ✅ All | ❌ | ❌ |
| **Python** | ❌ Never | ❌ | ✅ All | ❌ |
| **Angular** | ❌ Never | ❌ | ❌ | ✅ All |
| **PostgreSQL** | ✅ Data Store | ❌ | ❌ | ❌ |

## Communication Flow

### Frontend → Backend
```
Angular → HTTPS → Java REST API → PostgreSQL
         (JWT Auth)
```

### Python → Backend
```
Python → HTTPS → Java REST API → PostgreSQL
        (API Key)
```

### Python Workflow Example
```
1. Python Scheduler triggers task
2. Python calls: GET /api/v1/automation/recovery-cases/pending
3. Java queries database, returns cases
4. Python processes cases (extraction, reminders)
5. Python calls: PUT /api/v1/automation/recovery-cases/{id}
6. Java validates and persists to database
```

## Key Endpoints

### Frontend API
- `POST /api/v1/auth/login` - User authentication
- `GET /api/v1/recovery-cases` - List recovery cases
- `POST /api/v1/recovery-cases` - Create recovery case
- `PUT /api/v1/recovery-cases/{id}` - Update recovery case

### Automation API (Python → Java)
- `GET /api/v1/automation/recovery-cases/pending` - Get pending cases
- `PUT /api/v1/automation/recovery-cases/{id}` - Update case
- `POST /api/v1/automation/tasks` - Create scheduled task
- `GET /api/v1/automation/tasks/scheduled` - Get scheduled tasks
- `POST /api/v1/automation/extractions` - Submit extracted data
- `POST /api/v1/automation/reminders` - Create reminder
- `GET /api/v1/automation/documents/pending-extraction` - Get documents

## Technology Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| Frontend | Angular | User interface |
| Backend | Spring Boot | System of record, APIs |
| Automation | Python | Scheduling, reminders, extraction |
| Database | PostgreSQL | Data persistence |
| Cache | Redis | Session, data caching |
| Queue | RabbitMQ/Kafka (optional) | Async messaging |

## Security Model

### Authentication
- **Frontend:** JWT tokens (stored in httpOnly cookies)
- **Python Services:** API keys or OAuth2 client credentials
- **Service-to-Service:** Mutual TLS (mTLS) in production

### Authorization
- Role-Based Access Control (RBAC)
- Roles: Admin, Manager, Agent, Viewer
- Fine-grained permissions per resource

## Data Flow Examples

### Creating a Recovery Case
```
1. User submits form in Angular
2. Angular → POST /api/v1/recovery-cases (with JWT)
3. Java validates, persists to PostgreSQL
4. Java returns created case
5. Angular displays success message
```

### Sending a Reminder
```
1. Python scheduler triggers reminder task
2. Python → GET /api/v1/automation/recovery-cases/pending
3. Java returns overdue cases
4. Python generates reminder content
5. Python → POST /api/v1/automation/reminders
6. Java creates reminder record in database
7. Python sends email/SMS
8. Python → PUT /api/v1/automation/reminders/{id}/sent
9. Java updates reminder status
```

### Document Extraction
```
1. User uploads document in Angular
2. Angular → POST /api/v1/documents (with file)
3. Java stores file, creates document record
4. Python → GET /api/v1/automation/documents/pending-extraction
5. Java returns pending documents
6. Python extracts data from document
7. Python → POST /api/v1/automation/extractions
8. Java validates and stores extracted data
9. Java updates recovery case with extracted data
```

## Scalability Points

### Horizontal Scaling
- ✅ Java services (stateless)
- ✅ Python workers (stateless)
- ✅ Angular (static files, CDN)
- ⚠️ PostgreSQL (read replicas)

### Performance Optimization
- Redis caching for frequently accessed data
- Database connection pooling
- Query optimization and indexing
- Async processing for non-critical operations
- CDN for static assets

## Error Handling

### Java API Errors
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/recovery-cases",
  "details": [...]
}
```

### Python Retry Strategy
- Exponential backoff
- Max 3 retries
- Circuit breaker pattern
- Dead letter queue for failed tasks

## Monitoring Checklist

- [ ] Health checks for all services
- [ ] Request rate and latency metrics
- [ ] Error rate monitoring
- [ ] Database connection pool usage
- [ ] Cache hit rates
- [ ] Business metrics (cases, payments)
- [ ] Log aggregation and search
- [ ] Alerting on anomalies

## Deployment Checklist

- [ ] Environment variables configured
- [ ] Database migrations applied
- [ ] SSL certificates installed
- [ ] API keys generated
- [ ] Secrets management configured
- [ ] Monitoring and logging set up
- [ ] Backup strategy in place
- [ ] Disaster recovery plan documented

## Common Patterns

### Idempotency
- All write operations should be idempotent
- Use unique identifiers to prevent duplicates
- Check existence before creating

### Eventual Consistency
- Some operations may be async
- Use status fields to track progress
- Implement retry mechanisms

### Audit Trail
- All data changes are logged
- Include: user, timestamp, action, old/new values
- Never delete audit records

## Troubleshooting

### Python can't connect to Java API
- Check API base URL configuration
- Verify API key is correct
- Check network connectivity
- Review Java API logs

### Database connection issues
- Verify database credentials
- Check connection pool settings
- Review database logs
- Ensure database is accessible

### Performance issues
- Check database query performance
- Review cache hit rates
- Monitor connection pool usage
- Check for N+1 query problems

## Best Practices

1. **Never access database from Python** - Always use Java API
2. **Validate all inputs** - Both in Java and Python
3. **Log everything** - For debugging and audit
4. **Handle errors gracefully** - With proper error messages
5. **Use transactions** - For data consistency
6. **Implement retries** - For transient failures
7. **Monitor everything** - Metrics, logs, traces
8. **Test thoroughly** - Unit, integration, e2e tests

