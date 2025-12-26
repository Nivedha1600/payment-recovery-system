# Angular Project Structure - Multi-Role SaaS Application

## Overview

This document provides a detailed breakdown of the Angular project structure designed for a multi-role SaaS application with **ADMIN** and **COMPANY** roles.

## Architecture Principles

### 1. **Role-Based Module Separation**
- **Admin Module**: Separate module for ADMIN role features
- **Company Module**: Separate module for COMPANY role features
- **Auth Module**: Shared authentication for both roles

### 2. **Lazy Loading**
- Feature modules (admin, company) are lazy-loaded
- Improves initial load time
- Better code splitting

### 3. **Shared Resources**
- Core module for singleton services
- Shared module for reusable components
- Common layouts for different roles

## Detailed Structure

### 1. Core Module (`core/`)

**Purpose**: Singleton services, guards, and interceptors loaded once at application startup.

```
core/
├── guards/
│   ├── auth.guard.ts              # Protects authenticated routes
│   ├── role.guard.ts              # Protects role-based routes
│   └── index.ts                   # Barrel export
│
├── interceptors/
│   ├── auth.interceptor.ts        # Adds JWT token to requests
│   ├── error.interceptor.ts       # Global error handling
│   ├── loading.interceptor.ts     # Loading indicator
│   └── index.ts                   # Barrel export
│
├── services/
│   ├── auth.service.ts            # Authentication service
│   ├── token.service.ts           # Token storage/retrieval
│   ├── company.service.ts         # Current company context
│   ├── api.service.ts             # Base API service
│   └── index.ts                   # Barrel export
│
├── models/
│   ├── user.model.ts              # User interface
│   ├── company.model.ts           # Company interface
│   ├── role.model.ts              # Role enum/interface
│   └── index.ts                   # Barrel export
│
└── core.module.ts                 # Core module (imported once)
```

**Key Services**:

- **AuthService**: Handles login, logout, token refresh
- **TokenService**: Manages JWT token storage
- **CompanyService**: Manages current company context (for COMPANY role)
- **ApiService**: Base HTTP service with common methods

**Key Guards**:

- **AuthGuard**: Ensures user is authenticated
- **RoleGuard**: Ensures user has required role (ADMIN or COMPANY)

### 2. Shared Module (`shared/`)

**Purpose**: Reusable components, directives, pipes used across multiple modules.

```
shared/
├── components/
│   ├── header/
│   │   ├── header.component.ts
│   │   ├── header.component.html
│   │   └── header.component.scss
│   ├── sidebar/
│   │   ├── sidebar.component.ts
│   │   ├── sidebar.component.html
│   │   └── sidebar.component.scss
│   ├── loading-spinner/
│   ├── confirm-dialog/
│   ├── data-table/
│   ├── pagination/
│   └── index.ts
│
├── directives/
│   ├── click-outside.directive.ts
│   ├── auto-focus.directive.ts
│   └── index.ts
│
├── pipes/
│   ├── currency.pipe.ts
│   ├── date-format.pipe.ts
│   ├── truncate.pipe.ts
│   └── index.ts
│
├── validators/
│   ├── custom.validators.ts
│   └── index.ts
│
└── shared.module.ts
```

**Key Components**:

- **Header**: Navigation header (role-specific menus)
- **Sidebar**: Navigation sidebar (role-specific items)
- **DataTable**: Reusable data table with sorting, filtering
- **ConfirmDialog**: Confirmation dialog component

### 3. Auth Module (`auth/`)

**Purpose**: Authentication and authorization features (shared by both roles).

```
auth/
├── components/
│   ├── login/
│   │   ├── login.component.ts
│   │   ├── login.component.html
│   │   └── login.component.scss
│   ├── register/
│   ├── forgot-password/
│   └── reset-password/
│
├── services/
│   └── auth-api.service.ts        # Auth API calls
│
├── auth-routing.module.ts
└── auth.module.ts
```

**Routes**:
- `/auth/login` - Login page
- `/auth/register` - Registration (if applicable)
- `/auth/forgot-password` - Password recovery
- `/auth/reset-password/:token` - Password reset

### 4. Admin Module (`admin/`)

**Purpose**: Admin-only features (lazy-loaded, protected by RoleGuard).

```
admin/
├── components/
│   ├── dashboard/
│   │   ├── dashboard.component.ts
│   │   ├── dashboard.component.html
│   │   └── dashboard.component.scss
│   │
│   ├── companies/
│   │   ├── company-list/
│   │   │   ├── company-list.component.ts
│   │   │   ├── company-list.component.html
│   │   │   └── company-list.component.scss
│   │   ├── company-detail/
│   │   ├── company-form/
│   │   └── index.ts
│   │
│   ├── users/
│   │   ├── user-list/
│   │   ├── user-detail/
│   │   └── user-form/
│   │
│   ├── system-settings/
│   └── reports/
│
├── services/
│   ├── company-management.service.ts
│   ├── user-management.service.ts
│   └── admin-api.service.ts
│
├── models/
│   ├── admin-company.model.ts
│   └── admin-user.model.ts
│
├── admin-routing.module.ts
└── admin.module.ts
```

**Admin Routes**:
- `/admin/dashboard` - Admin dashboard
- `/admin/companies` - Company management
- `/admin/companies/:id` - Company details
- `/admin/users` - User management
- `/admin/settings` - System settings
- `/admin/reports` - System reports

**Admin Features**:
- View all companies
- Create/edit/delete companies
- Manage users across all companies
- System-wide settings
- Analytics and reports

### 5. Company Module (`company/`)

**Purpose**: Company user features (lazy-loaded, protected by RoleGuard).

```
company/
├── components/
│   ├── dashboard/
│   │   ├── dashboard.component.ts
│   │   ├── dashboard.component.html
│   │   └── dashboard.component.scss
│   │
│   ├── invoices/
│   │   ├── invoice-list/
│   │   │   ├── invoice-list.component.ts
│   │   │   ├── invoice-list.component.html
│   │   │   └── invoice-list.component.scss
│   │   ├── invoice-detail/
│   │   ├── invoice-upload/
│   │   ├── invoice-form/
│   │   └── index.ts
│   │
│   ├── customers/
│   │   ├── customer-list/
│   │   ├── customer-detail/
│   │   └── customer-form/
│   │
│   ├── payments/
│   │   ├── payment-list/
│   │   ├── payment-detail/
│   │   └── payment-form/
│   │
│   ├── reminders/
│   │   ├── reminder-list/
│   │   └── reminder-history/
│   │
│   └── settings/
│       ├── company-profile/
│       └── user-settings/
│
├── services/
│   ├── invoice.service.ts
│   ├── customer.service.ts
│   ├── payment.service.ts
│   └── company-api.service.ts
│
├── models/
│   ├── invoice.model.ts
│   ├── customer.model.ts
│   └── payment.model.ts
│
├── company-routing.module.ts
└── company.module.ts
```

**Company Routes**:
- `/company/dashboard` - Company dashboard
- `/company/invoices` - Invoice list
- `/company/invoices/:id` - Invoice details
- `/company/invoices/upload` - Upload invoice
- `/company/customers` - Customer management
- `/company/payments` - Payment tracking
- `/company/reminders` - Reminder management
- `/company/settings` - Company settings

**Company Features**:
- Manage own invoices
- Manage own customers
- Track payments
- View reminders
- Company profile settings

### 6. Layout Module (`layout/`)

**Purpose**: Application shell components for different contexts.

```
layout/
├── admin-layout/
│   ├── admin-layout.component.ts
│   ├── admin-layout.component.html
│   └── admin-layout.component.scss
│
├── company-layout/
│   ├── company-layout.component.ts
│   ├── company-layout.component.html
│   └── company-layout.component.scss
│
└── auth-layout/
    ├── auth-layout.component.ts
    ├── auth-layout.component.html
    └── auth-layout.component.scss
```

**Layout Components**:

- **AdminLayout**: Layout for admin routes (admin sidebar, admin header)
- **CompanyLayout**: Layout for company routes (company sidebar, company header)
- **AuthLayout**: Layout for authentication pages (centered, no sidebar)

## Routing Structure

### Root Routing (`app-routing.module.ts`)

```typescript
const routes: Routes = [
  // Auth routes (no authentication required)
  {
    path: 'auth',
    component: AuthLayoutComponent,
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule)
  },
  
  // Admin routes (requires ADMIN role)
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN'] },
    loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule)
  },
  
  // Company routes (requires COMPANY role)
  {
    path: 'company',
    component: CompanyLayoutComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['COMPANY'] },
    loadChildren: () => import('./company/company.module').then(m => m.CompanyModule)
  },
  
  // Default redirect
  { path: '', redirectTo: '/auth/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/auth/login' }
];
```

### Admin Routing (`admin/admin-routing.module.ts`)

```typescript
const routes: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'companies', component: CompanyListComponent },
      { path: 'companies/:id', component: CompanyDetailComponent },
      { path: 'users', component: UserListComponent },
      { path: 'settings', component: SystemSettingsComponent },
      { path: 'reports', component: ReportsComponent },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  }
];
```

### Company Routing (`company/company-routing.module.ts`)

```typescript
const routes: Routes = [
  {
    path: '',
    component: CompanyLayoutComponent,
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'invoices', component: InvoiceListComponent },
      { path: 'invoices/:id', component: InvoiceDetailComponent },
      { path: 'invoices/upload', component: InvoiceUploadComponent },
      { path: 'customers', component: CustomerListComponent },
      { path: 'payments', component: PaymentListComponent },
      { path: 'reminders', component: ReminderListComponent },
      { path: 'settings', component: CompanySettingsComponent },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  }
];
```

## Role-Based Access Control

### Role Guard Implementation

```typescript
@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const requiredRoles = route.data['roles'] as string[];
    const userRole = this.authService.getCurrentUser()?.role;

    if (!requiredRoles || requiredRoles.includes(userRole)) {
      return true;
    }

    // Redirect based on role
    if (userRole === 'ADMIN') {
      this.router.navigate(['/admin/dashboard']);
    } else if (userRole === 'COMPANY') {
      this.router.navigate(['/company/dashboard']);
    } else {
      this.router.navigate(['/auth/login']);
    }

    return false;
  }
}
```

### Role-Based Menu Items

**Admin Sidebar**:
- Dashboard
- Companies
- Users
- System Settings
- Reports

**Company Sidebar**:
- Dashboard
- Invoices
- Customers
- Payments
- Reminders
- Settings

## Service Organization

### Core Services (Singleton)
- `AuthService` - Authentication state
- `TokenService` - Token management
- `CompanyService` - Current company context

### Feature Services
- **Admin**: `CompanyManagementService`, `UserManagementService`
- **Company**: `InvoiceService`, `CustomerService`, `PaymentService`

## Best Practices

### 1. Path Aliases

Configure in `tsconfig.json`:

```json
{
  "compilerOptions": {
    "paths": {
      "@core/*": ["src/app/core/*"],
      "@shared/*": ["src/app/shared/*"],
      "@auth/*": ["src/app/auth/*"],
      "@admin/*": ["src/app/admin/*"],
      "@company/*": ["src/app/company/*"],
      "@layout/*": ["src/app/layout/*"],
      "@env/*": ["src/environments/*"]
    }
  }
}
```

### 2. Lazy Loading

Always lazy-load feature modules:

```typescript
{
  path: 'admin',
  loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule)
}
```

### 3. OnPush Change Detection

Use `OnPush` for better performance:

```typescript
@Component({
  selector: 'app-invoice-list',
  changeDetection: ChangeDetectionStrategy.OnPush
})
```

### 4. Barrel Exports

Use index.ts for cleaner imports:

```typescript
// core/guards/index.ts
export * from './auth.guard';
export * from './role.guard';
```

### 5. Role-Based Component Visibility

Use `*ngIf` with role checks:

```html
<button *ngIf="userRole === 'ADMIN'" (click)="deleteCompany()">
  Delete Company
</button>
```

## Security Considerations

1. **Route Guards**: Protect all routes with AuthGuard and RoleGuard
2. **HTTP Interceptors**: Add JWT token to all API requests
3. **Token Storage**: Use secure storage (httpOnly cookies or secure localStorage)
4. **Role Validation**: Validate roles on both client and server
5. **Component Guards**: Hide UI elements based on roles

## Summary

This structure provides:

✅ **Clear Role Separation**: Admin and Company modules are completely separate  
✅ **Scalability**: Easy to add new features to each role  
✅ **Security**: Role-based guards and route protection  
✅ **Performance**: Lazy loading for feature modules  
✅ **Maintainability**: Organized and predictable structure  
✅ **Reusability**: Shared components and services  

