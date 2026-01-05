# Factory Autopilot - Payment Recovery System
## Production Readiness Plan

## Critical Gaps Identified

### 1. ✅ Invoice Status Flow (PARTIALLY FIXED)
**Current State:**
- DRAFT status exists ✅
- PENDING status exists (used as ACTIVE) ✅
- PAID status exists ✅
- **MISSING:** confirmInvoice() method to move DRAFT → PENDING

**Action Required:**
- Add `confirmInvoice()` endpoint and service method
- Ensure DRAFT invoices are excluded from reminders ✅ (already done)
- Ensure DRAFT invoices are excluded from dashboard ✅ (already done)

### 2. ✅ Company Dashboard (FIXED)
**Status:** Company dashboard metrics endpoint created and working
- Uses CompanyService (proper service layer)
- Follows existing patterns
- Proper error handling

### 3. ✅ Reminder Automation (VERIFIED)
**Status:** Python correctly fetches only PENDING invoices
- Java API returns only PENDING status invoices ✅
- Python processes only PENDING invoices ✅
- DRAFT invoices are excluded ✅

### 4. ⚠️ Invoice Confirmation Flow (MISSING)
**Gap:** No way to confirm DRAFT invoice to ACTIVE/PENDING status

**Required Implementation:**
```java
POST /api/invoices/{id}/confirm
- Validates invoice is in DRAFT status
- Validates extracted data exists
- Updates invoice fields from extracted data
- Changes status from DRAFT → PENDING
- Returns confirmed invoice
```

### 5. ✅ Security (VERIFIED)
- Company endpoints secured ✅
- Role-based access control ✅
- JWT authentication ✅

## Status Mapping (Important)

**Spec says:** DRAFT → ACTIVE → PAID
**Code uses:** DRAFT → PENDING → PAID

**Decision:** Treat PENDING as ACTIVE in this system
- PENDING = invoices ready for reminders (ACTIVE in spec)
- This is already implemented correctly
- No enum change needed (would break existing code)

## Implementation Checklist

### Backend (Java)
- [x] Company dashboard metrics endpoint
- [x] Invoice upload creates DRAFT
- [x] Python extraction stores data (stays DRAFT)
- [ ] **MISSING:** Invoice confirmation (DRAFT → PENDING)
- [x] Payment marking (PENDING → PAID)
- [x] Reminders only fetch PENDING invoices
- [x] Dashboard only counts PENDING invoices

### Frontend (Angular)
- [x] Admin dashboard
- [x] Company dashboard
- [x] Company management (approve/activate)
- [ ] **MISSING:** Draft invoice review page
- [ ] **MISSING:** Invoice confirmation UI

### Python Automation
- [x] Invoice extraction service
- [x] Reminder automation service
- [x] Only processes PENDING invoices
- [x] Proper error handling

## Next Steps

1. **IMMEDIATE:** Add invoice confirmation endpoint
2. **IMMEDIATE:** Add draft invoice review UI
3. **VERIFY:** Test end-to-end flow
4. **DOCUMENT:** Status mapping (PENDING = ACTIVE)

