# Database Setup Guide

## Quick Fix for Schema Validation Error

If you're getting the error: `Schema-validation: missing table [companies]`, you have two options:

### Option 1: Auto-create Tables (Development - Recommended)

The `application.properties` is now configured with `spring.jpa.hibernate.ddl-auto=update` which will automatically create/update tables when the application starts.

**Just restart your application** and the tables will be created automatically.

### Option 2: Manual Schema Creation

If you prefer to create tables manually, run the SQL script:

```bash
psql -U postgres -d payment_recovery_db -f src/main/resources/db/schema.sql
```

Or connect to PostgreSQL and run:

```sql
\i src/main/resources/db/schema.sql
```

## Database Configuration

### Create Database

```sql
CREATE DATABASE payment_recovery_db;
```

### Connection Settings

Update `application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/payment_recovery_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

Or use environment variables:

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
```

## Profile-Based Configuration

### Development Profile (`application-dev.properties`)
- `ddl-auto=update` - Auto-create/update tables
- `show-sql=true` - Show SQL queries
- Debug logging enabled

**Run with dev profile:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Production Profile (`application-prod.properties`)
- `ddl-auto=validate` - Only validate schema (tables must exist)
- `show-sql=false` - Don't show SQL queries
- Production logging levels

**Run with prod profile:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## Schema Management Options

### 1. Hibernate Auto DDL (Current - Development)
- **Pros**: Quick setup, automatic
- **Cons**: Not suitable for production, no version control
- **Use**: Development only

### 2. Flyway Migrations (Recommended for Production)
- **Pros**: Version controlled, production-ready, rollback support
- **Cons**: Requires setup
- **Use**: Production environments

To enable Flyway, uncomment in `application.properties`:
```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
```

Then create migration files in `src/main/resources/db/migration/`:
- `V1__Create_companies_table.sql`
- `V2__Create_users_table.sql`
- etc.

### 3. Liquibase (Alternative)
Similar to Flyway, provides database migration management.

## Troubleshooting

### Error: "Schema-validation: missing table"
**Solution**: Change `ddl-auto` from `validate` to `update` in `application.properties`

### Error: "Connection refused"
**Solution**: 
1. Ensure PostgreSQL is running
2. Check connection URL, username, and password
3. Verify database exists: `CREATE DATABASE payment_recovery_db;`

### Error: "Permission denied"
**Solution**: Grant necessary permissions to your database user

## Tables Created

The schema includes the following tables:
1. **companies** - Multi-tenant root entity
2. **users** - System users
3. **customers** - Customer information
4. **invoices** - Invoice records
5. **payments** - Payment records
6. **reminder_logs** - Reminder tracking

All tables include:
- Primary key (id)
- Created/updated timestamps
- Appropriate indexes
- Foreign key constraints
- Check constraints where applicable

