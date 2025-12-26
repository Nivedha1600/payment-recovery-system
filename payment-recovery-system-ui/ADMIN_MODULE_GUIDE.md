# Admin Module Guide

## Overview

The Admin module provides administrative functionality for managing the platform, including company management and platform-level metrics.

## Module Structure

```
admin/
├── components/
│   ├── dashboard/
│   │   ├── dashboard.component.ts
│   │   ├── dashboard.component.html
│   │   └── dashboard.component.scss
│   └── company-management/
│       ├── company-management.component.ts
│       ├── company-management.component.html
│       └── company-management.component.scss
├── services/
│   └── admin-api.service.ts
├── models/
│   ├── company.model.ts
│   └── dashboard.model.ts
├── admin.module.ts
└── admin-routing.module.ts
```

## Components

### 1. AdminDashboardComponent

**Route**: `/admin/dashboard`

**Purpose**: Displays platform-level metrics and overview

**Features**:
- Total companies (active/inactive breakdown)
- Total users
- Total invoices (pending/paid breakdown)
- Total revenue
- Recent activity feed

**API Endpoints Used**:
- `GET /api/admin/metrics` - Get platform metrics

### 2. CompanyManagementComponent

**Route**: `/admin/companies`

**Purpose**: Manage registered companies

**Features**:
- View all registered companies
- Search companies by name or GST number
- Activate/deactivate companies
- View company statistics (users, invoices)
- Pagination support

**API Endpoints Used**:
- `GET /api/admin/companies` - Get companies list (with pagination and search)
- `GET /api/admin/companies/:id` - Get company details
- `PATCH /api/admin/companies/:id/status` - Update company status

## Services

### AdminApiService

**Location**: `admin/services/admin-api.service.ts`

**Methods**:

```typescript
// Get platform metrics
getPlatformMetrics(): Observable<PlatformMetrics>

// Get companies list
getCompanies(page: number, pageSize: number, search?: string): Observable<CompanyListResponse>

// Get company by ID
getCompanyById(companyId: number): Observable<Company>

// Update company status
updateCompanyStatus(companyId: number, isActive: boolean): Observable<Company>

// Activate company
activateCompany(companyId: number): Observable<Company>

// Deactivate company
deactivateCompany(companyId: number): Observable<Company>
```

## Models

### Company Model

```typescript
interface Company {
  id: number;
  name: string;
  gstNumber?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt?: string;
  userCount?: number;
  invoiceCount?: number;
}
```

### Platform Metrics Model

```typescript
interface PlatformMetrics {
  totalCompanies: number;
  activeCompanies: number;
  inactiveCompanies: number;
  totalUsers: number;
  totalInvoices: number;
  pendingInvoices: number;
  paidInvoices: number;
  totalRevenue: number;
  recentActivity: ActivityItem[];
}
```

## Routing

### Admin Routes

```typescript
{
  path: 'admin',
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['ADMIN'] },
  loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule)
}
```

### Internal Routes

```typescript
{
  path: 'dashboard',
  component: AdminDashboardComponent
},
{
  path: 'companies',
  component: CompanyManagementComponent
}
```

## Features

### Dashboard Features

1. **Platform Metrics Cards**
   - Total Companies (with active/inactive breakdown)
   - Total Users
   - Total Invoices (with pending/paid breakdown)
   - Total Revenue

2. **Recent Activity Feed**
   - Shows recent platform activities
   - Displays activity type, description, and timestamp

3. **Refresh Functionality**
   - Manual refresh button to reload metrics

### Company Management Features

1. **Company List View**
   - Table view with all company information
   - Sortable columns
   - Status indicators (Active/Inactive)

2. **Search Functionality**
   - Search by company name
   - Search by GST number
   - Real-time search with Enter key support

3. **Company Status Management**
   - Activate inactive companies
   - Deactivate active companies
   - Confirmation dialog before status change
   - Loading state during update

4. **Pagination**
   - Page-based pagination
   - Configurable page size (default: 10)
   - Shows current page and total pages
   - Previous/Next navigation

## Backend API Requirements

### Expected Endpoints

#### 1. Get Platform Metrics
```
GET /api/admin/metrics
Response: PlatformMetrics
```

#### 2. Get Companies List
```
GET /api/admin/companies?page=0&size=10&search=query
Response: CompanyListResponse
```

#### 3. Get Company by ID
```
GET /api/admin/companies/:id
Response: Company
```

#### 4. Update Company Status
```
PATCH /api/admin/companies/:id/status
Body: { companyId: number, isActive: boolean }
Response: Company
```

## Usage Examples

### Accessing Admin Dashboard

```typescript
// Navigate to admin dashboard
this.router.navigate(['/admin/dashboard']);
```

### Accessing Company Management

```typescript
// Navigate to company management
this.router.navigate(['/admin/companies']);
```

### Using AdminApiService

```typescript
constructor(private adminApiService: AdminApiService) {}

// Get platform metrics
this.adminApiService.getPlatformMetrics().subscribe(metrics => {
  console.log('Total companies:', metrics.totalCompanies);
});

// Get companies
this.adminApiService.getCompanies(0, 10, 'search term').subscribe(response => {
  console.log('Companies:', response.companies);
});

// Activate company
this.adminApiService.activateCompany(companyId).subscribe(company => {
  console.log('Company activated:', company);
});
```

## Security

- All admin routes are protected by:
  - **AuthGuard**: Ensures user is authenticated
  - **RoleGuard**: Ensures user has ADMIN role
- All API calls include JWT token via AuthInterceptor
- Backend should validate ADMIN role for all admin endpoints

## Styling

- Uses SCSS for component styles
- Responsive design for mobile devices
- Consistent color scheme:
  - Primary: #667eea
  - Success: #48bb78
  - Danger: #f56565
  - Active: Green shades
  - Inactive: Red shades

## Next Steps

1. Implement user management component
2. Add system settings component
3. Add reports/analytics component
4. Implement export functionality for company data
5. Add bulk operations (activate/deactivate multiple companies)

