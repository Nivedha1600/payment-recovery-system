-- Payment Recovery System Database Schema
-- PostgreSQL Database Creation Script

-- Create Companies Table
CREATE TABLE IF NOT EXISTS companies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    gst_number VARCHAR(50) UNIQUE,
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_approved BOOLEAN NOT NULL DEFAULT false,
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create Indexes for Companies
CREATE INDEX IF NOT EXISTS idx_company_gst_number ON companies(gst_number);
CREATE INDEX IF NOT EXISTS idx_company_is_active ON companies(is_active);
CREATE INDEX IF NOT EXISTS idx_company_is_approved ON companies(is_approved);

-- Create Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_user_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT uk_user_username_company UNIQUE (username, company_id)
);

-- Create Indexes for Users
CREATE INDEX IF NOT EXISTS idx_user_company_id ON users(company_id);
CREATE INDEX IF NOT EXISTS idx_user_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_user_is_active ON users(is_active);

-- Create Customers Table
CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    company_name VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(255),
    payment_terms_days INTEGER NOT NULL DEFAULT 30,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_customer_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

-- Create Indexes for Customers
CREATE INDEX IF NOT EXISTS idx_customer_company_id ON customers(company_id);
CREATE INDEX IF NOT EXISTS idx_customer_email ON customers(email);
CREATE INDEX IF NOT EXISTS idx_customer_phone ON customers(phone);

-- Create Invoices Table
CREATE TABLE IF NOT EXISTS invoices (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    customer_id BIGINT,
    invoice_number VARCHAR(100),
    invoice_date DATE,
    due_date DATE,
    amount NUMERIC(19, 2),
    file_path VARCHAR(500),
    extracted_data JSONB,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_invoice_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_invoice_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Create Indexes for Invoices
CREATE INDEX IF NOT EXISTS idx_invoice_company_id ON invoices(company_id);
CREATE INDEX IF NOT EXISTS idx_invoice_customer_id ON invoices(customer_id);
CREATE INDEX IF NOT EXISTS idx_invoice_invoice_number ON invoices(invoice_number);
CREATE INDEX IF NOT EXISTS idx_invoice_status ON invoices(status);
CREATE INDEX IF NOT EXISTS idx_invoice_due_date ON invoices(due_date);

-- Create Payments Table
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    amount_received NUMERIC(19, 2) NOT NULL,
    payment_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_payment_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id),
    CONSTRAINT chk_payment_amount CHECK (amount_received > 0)
);

-- Create Indexes for Payments
CREATE INDEX IF NOT EXISTS idx_payment_invoice_id ON payments(invoice_id);
CREATE INDEX IF NOT EXISTS idx_payment_payment_date ON payments(payment_date);

-- Create Reminder Logs Table
CREATE TABLE IF NOT EXISTS reminder_logs (
    id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    reminder_type VARCHAR(20) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    sent_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create Indexes for Reminder Logs
CREATE INDEX IF NOT EXISTS idx_reminder_log_invoice_id ON reminder_logs(invoice_id);
CREATE INDEX IF NOT EXISTS idx_reminder_log_sent_date ON reminder_logs(sent_date);
CREATE INDEX IF NOT EXISTS idx_reminder_log_type_channel ON reminder_logs(reminder_type, channel);

-- Comments for documentation
COMMENT ON TABLE companies IS 'Multi-tenant root entity - represents a company/tenant';
COMMENT ON TABLE users IS 'Users belonging to a company';
COMMENT ON TABLE customers IS 'Customers belonging to a company';
COMMENT ON TABLE invoices IS 'Invoices belonging to a company and customer';
COMMENT ON TABLE payments IS 'Payments received for invoices';
COMMENT ON TABLE reminder_logs IS 'Log of reminders sent to customers';

