-- Migration: Add company approval and contact fields
-- Run this script to update existing database schema

-- Add is_approved column to companies table
ALTER TABLE companies 
ADD COLUMN IF NOT EXISTS is_approved BOOLEAN NOT NULL DEFAULT false;

-- Add contact_email column to companies table
ALTER TABLE companies 
ADD COLUMN IF NOT EXISTS contact_email VARCHAR(255);

-- Add contact_phone column to companies table
ALTER TABLE companies 
ADD COLUMN IF NOT EXISTS contact_phone VARCHAR(20);

-- Create index for is_approved for faster queries
CREATE INDEX IF NOT EXISTS idx_company_is_approved ON companies(is_approved);

-- Update existing companies (except admin) to be approved if they are active
-- This ensures existing companies can still login
UPDATE companies 
SET is_approved = true 
WHERE is_active = true 
  AND (gst_number IS NULL OR gst_number != 'ADMIN-GST-001');

-- Admin company should be approved
UPDATE companies 
SET is_approved = true 
WHERE gst_number = 'ADMIN-GST-001';

