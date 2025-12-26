# JPA Entities Summary

## Created Entities

### 1. **BaseEntity** (Abstract)
Base class for all entities with common fields:
- `id` (Long, auto-generated)
- `createdAt` (LocalDateTime, auto-populated)
- `updatedAt` (LocalDateTime, auto-populated)

**Features:**
- Uses `@MappedSuperclass` for inheritance
- JPA Auditing enabled for automatic timestamp management
- All entities extend this base class

### 2. **Company** Entity
Multi-tenant root entity representing a company/tenant.

**Fields:**
- `id` (inherited from BaseEntity)
- `name` (String, required)
- `gstNumber` (String, unique)
- `isActive` (Boolean, default: true)
- `createdAt` (inherited from BaseEntity)
- `updatedAt` (inherited from BaseEntity)

**Indexes:**
- GST number index
- Is active index

### 3. **User** Entity
Represents a user in the system. Belongs to a Company.

**Fields:**
- `id` (inherited from BaseEntity)
- `company` (ManyToOne relationship with Company)
- `username` (String, required)
- `password` (String, required - to be encrypted)
- `role` (UserRole enum: ADMIN, ACCOUNT)
- `isActive` (Boolean, default: true)
- `createdAt` (inherited from BaseEntity)
- `updatedAt` (inherited from BaseEntity)

**Constraints:**
- Unique constraint on (username, company_id) - username unique per company
- Foreign key to Company

**Indexes:**
- Company ID index
- Username index
- Is active index

### 4. **Customer** Entity
Represents a customer. Belongs to a Company.

**Fields:**
- `id` (inherited from BaseEntity)
- `company` (ManyToOne relationship with Company)
- `customerName` (String, required)
- `companyName` (String, optional)
- `phone` (String, optional)
- `email` (String, optional, validated)
- `paymentTermsDays` (Integer, default: 30)
- `createdAt` (inherited from BaseEntity)
- `updatedAt` (inherited from BaseEntity)

**Indexes:**
- Company ID index
- Email index
- Phone index

### 5. **Invoice** Entity
Represents an invoice. Belongs to a Company and a Customer.

**Fields:**
- `id` (inherited from BaseEntity)
- `company` (ManyToOne relationship with Company)
- `customer` (ManyToOne relationship with Customer)
- `invoiceNumber` (String, required)
- `invoiceDate` (LocalDate, required)
- `dueDate` (LocalDate, required)
- `amount` (BigDecimal, required, min: 0.01)
- `status` (InvoiceStatus enum: PENDING, PARTIAL, PAID, default: PENDING)
- `createdAt` (inherited from BaseEntity)
- `updatedAt` (inherited from BaseEntity)

**Constraints:**
- Unique constraint on (invoice_number, company_id) - invoice number unique per company
- Foreign keys to Company and Customer

**Indexes:**
- Company ID index
- Customer ID index
- Invoice number index
- Status index
- Due date index

### 6. **Payment** Entity
Represents a payment received. Belongs to an Invoice.

**Fields:**
- `id` (inherited from BaseEntity)
- `invoice` (ManyToOne relationship with Invoice)
- `amountReceived` (BigDecimal, required, min: 0.01)
- `paymentDate` (LocalDate, required)
- `createdAt` (inherited from BaseEntity)
- `updatedAt` (inherited from BaseEntity)

**Indexes:**
- Invoice ID index
- Payment date index

### 7. **ReminderLog** Entity
Tracks reminder communications sent to customers.

**Fields:**
- `id` (inherited from BaseEntity)
- `invoiceId` (Long, required)
- `reminderType` (ReminderType enum: GENTLE, DUE, FIRM, ESCALATION)
- `channel` (ReminderChannel enum: WHATSAPP, EMAIL)
- `sentDate` (LocalDateTime, required)
- `createdAt` (inherited from BaseEntity)
- `updatedAt` (inherited from BaseEntity)

**Indexes:**
- Invoice ID index
- Sent date index
- Composite index on (reminder_type, channel)

## Enumerations

### UserRole
- `ADMIN` - Administrator role
- `ACCOUNT` - Account role

### InvoiceStatus
- `PENDING` - Invoice is pending payment
- `PARTIAL` - Partial payment received
- `PAID` - Invoice is fully paid

### ReminderType
- `GENTLE` - Gentle reminder
- `DUE` - Due date reminder
- `FIRM` - Firm reminder
- `ESCALATION` - Escalation reminder

### ReminderChannel
- `WHATSAPP` - WhatsApp channel
- `EMAIL` - Email channel

## Entity Relationships

```
Company (1) ──< (Many) User
Company (1) ──< (Many) Customer
Company (1) ──< (Many) Invoice
Customer (1) ──< (Many) Invoice
Invoice (1) ──< (Many) Payment
Invoice (1) ──< (Many) ReminderLog (via invoiceId)
```

## Key Features

### Multi-Tenancy Support
- All entities (except Company) have a `company` relationship
- Data isolation per company/tenant
- Company is the root tenant entity

### Data Integrity
- Foreign key constraints on all relationships
- Unique constraints where appropriate
- Not null constraints on required fields
- Validation annotations (Bean Validation)

### Performance Optimization
- Strategic indexes on frequently queried columns
- Lazy loading for relationships (FetchType.LAZY)
- Composite indexes for common query patterns

### Audit Trail
- Automatic `createdAt` and `updatedAt` timestamps
- JPA Auditing enabled via `@CreatedDate` and `@LastModifiedDate`

### Soft Deletes
- `isActive` flags on Company and User entities
- Allows soft deletion without data loss

## Usage Notes

### Password Encryption
The `User.password` field is currently a plain String. You should:
1. Use Spring Security's `BCryptPasswordEncoder` or similar
2. Encrypt passwords before persisting
3. Never store plain text passwords

### ReminderLog Invoice Reference
`ReminderLog` uses `invoiceId` (Long) instead of a JPA relationship. This is intentional to:
- Allow logging reminders even if invoice is deleted
- Maintain audit trail integrity
- Reduce coupling

### BigDecimal for Money
All monetary fields (`amount`, `amountReceived`) use `BigDecimal` to:
- Avoid floating-point precision issues
- Ensure accurate financial calculations
- Support high precision (19 digits, 2 decimal places)

## Next Steps

1. **Create Repositories**
   - `CompanyRepository`
   - `UserRepository`
   - `CustomerRepository`
   - `InvoiceRepository`
   - `PaymentRepository`
   - `ReminderLogRepository`

2. **Create Services**
   - Business logic for each entity
   - Password encryption service
   - Invoice status calculation logic

3. **Create DTOs**
   - Request DTOs for API endpoints
   - Response DTOs for API responses

4. **Database Migration**
   - Create Flyway or Liquibase migration scripts
   - Or use `spring.jpa.hibernate.ddl-auto=update` for development

