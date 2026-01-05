# Database Migration Guide

## Quick Fix: Add Company Approval Columns

The database needs to be updated with new columns for company approval functionality.

### Option 1: Run Migration Script (Recommended)

Connect to PostgreSQL and run:

```bash
psql -U postgres -d payment_recovery_db -f src/main/resources/db/migration_add_company_approval.sql
```

Or using psql interactive mode:

```bash
psql -U postgres -d payment_recovery_db
```

Then copy and paste the SQL commands from `migration_add_company_approval.sql`.

### Option 2: Run SQL Commands Directly

Connect to your database and run these commands:

```sql
-- Add is_approved column
ALTER TABLE companies 
ADD COLUMN IF NOT EXISTS is_approved BOOLEAN NOT NULL DEFAULT false;

-- Add contact_email column
ALTER TABLE companies 
ADD COLUMN IF NOT EXISTS contact_email VARCHAR(255);

-- Add contact_phone column
ALTER TABLE companies 
ADD COLUMN IF NOT EXISTS contact_phone VARCHAR(20);

-- Create index
CREATE INDEX IF NOT EXISTS idx_company_is_approved ON companies(is_approved);

-- Update existing companies to be approved
UPDATE companies 
SET is_approved = true 
WHERE is_active = true 
  AND (gst_number IS NULL OR gst_number != 'ADMIN-GST-001');

-- Ensure admin company is approved
UPDATE companies 
SET is_approved = true 
WHERE gst_number = 'ADMIN-GST-001';
```

### Option 3: Let Hibernate Auto-Update (If enabled)

If `spring.jpa.hibernate.ddl-auto=update` is set in `application.properties`, 
simply restart the application and Hibernate will add the missing columns automatically.

**Note:** Make sure the application has write permissions to the database.

