# Role-Based Routing Implementation

## Overview

This document describes the role-based routing implementation in the Angular application, including authentication guards, role guards, and route protection.

## Guards

### 1. AuthGuard

**Purpose**: Checks if user is authenticated before allowing route access.

**Location**: `src/app/core/guards/auth.guard.ts`

**Behavior**:
- Checks if user has a valid authentication token
- If authenticated: allows route access
- If not authenticated: redirects to `/auth/login` with `returnUrl` query parameter

**Usage**:
```typescript
{
  path: 'protected-route',
  canActivate: [AuthGuard],
  component: ProtectedComponent
}
```

### 2. RoleGuard

**Purpose**: Checks if authenticated user has the required role(s) to access a route.

**Location**: `src/app/core/guards/role.guard.ts`

**Behavior**:
- First checks if user is authenticated (should already be checked by AuthGuard)
- Gets required roles from route `data.roles`
- Checks if user's role matches one of the required roles
- If role matches: allows route access
- If role mismatch: redirects to `/access-denied` or appropriate dashboard

**Usage**:
```typescript
{
  path: 'admin-route',
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['ADMIN'] },
  component: AdminComponent
}
```

## Route Configuration

### Root Routes (`app-routing.module.ts`)

```typescript
const routes: Routes = [
  // Auth routes - No guards (public access)
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule)
  },
  
  // Admin routes - Requires ADMIN role
  {
    path: 'admin',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN'] },
    loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule)
  },
  
  // Company routes - Requires COMPANY role
  {
    path: 'company',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['COMPANY'] },
    loadChildren: () => import('./company/company.module').then(m => m.CompanyModule)
  },
  
  // Access Denied page
  {
    path: 'access-denied',
    loadChildren: () => import('./shared/components/access-denied/access-denied.module').then(m => m.AccessDeniedModule)
  }
];
```

### Guard Execution Order

When a route has multiple guards, they execute in order:

1. **AuthGuard** executes first
   - If fails → redirects to `/auth/login`
   - If passes → continues to next guard

2. **RoleGuard** executes second
   - If fails → redirects to `/access-denied` or appropriate dashboard
   - If passes → route is accessible

## Route Protection Examples

### Admin Routes

All routes under `/admin/**` are protected:

```typescript
// app-routing.module.ts
{
  path: 'admin',
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['ADMIN'] },
  loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule)
}

// admin-routing.module.ts
const routes: Routes = [
  {
    path: 'dashboard',
    component: AdminDashboardComponent // Only ADMIN can access
  },
  {
    path: 'companies',
    component: CompanyListComponent // Only ADMIN can access
  }
];
```

**Result**: 
- ✅ ADMIN user → Can access all `/admin/**` routes
- ❌ COMPANY user → Redirected to `/access-denied` or `/company/dashboard`
- ❌ Unauthenticated user → Redirected to `/auth/login`

### Company Routes

All routes under `/company/**` are protected:

```typescript
// app-routing.module.ts
{
  path: 'company',
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['COMPANY'] },
  loadChildren: () => import('./company/company.module').then(m => m.CompanyModule)
}

// company-routing.module.ts
const routes: Routes = [
  {
    path: 'dashboard',
    component: CompanyDashboardComponent // Only COMPANY can access
  },
  {
    path: 'invoices',
    component: InvoiceListComponent // Only COMPANY can access
  }
];
```

**Result**: 
- ✅ COMPANY user → Can access all `/company/**` routes
- ❌ ADMIN user → Redirected to `/access-denied` or `/admin/dashboard`
- ❌ Unauthenticated user → Redirected to `/auth/login`

## Access Denied Page

### Purpose

The access denied page is displayed when:
- User tries to access a route they don't have permission for
- Role mismatch occurs (e.g., COMPANY user tries to access `/admin/**`)

### Location

- Component: `src/app/shared/components/access-denied/access-denied.component.ts`
- Route: `/access-denied`

### Features

- Displays error message
- Shows user's current role
- Shows attempted URL (if available)
- Provides navigation buttons:
  - "Go to Dashboard" - Redirects to user's appropriate dashboard
  - "Go Back" - Returns to previous page (if attempted URL exists)

## Redirect Flow

### Scenario 1: Unauthenticated User

```
User tries to access: /admin/dashboard
↓
AuthGuard checks: ❌ Not authenticated
↓
Redirect to: /auth/login?returnUrl=/admin/dashboard
```

### Scenario 2: COMPANY User Tries Admin Route

```
User (COMPANY) tries to access: /admin/dashboard
↓
AuthGuard checks: ✅ Authenticated
↓
RoleGuard checks: ❌ Role mismatch (COMPANY ≠ ADMIN)
↓
Redirect to: /access-denied?attemptedUrl=/admin/dashboard&userRole=COMPANY
```

### Scenario 3: ADMIN User Tries Company Route

```
User (ADMIN) tries to access: /company/dashboard
↓
AuthGuard checks: ✅ Authenticated
↓
RoleGuard checks: ❌ Role mismatch (ADMIN ≠ COMPANY)
↓
Redirect to: /access-denied?attemptedUrl=/company/dashboard&userRole=ADMIN
```

### Scenario 4: Correct Role Access

```
User (ADMIN) tries to access: /admin/dashboard
↓
AuthGuard checks: ✅ Authenticated
↓
RoleGuard checks: ✅ Role matches (ADMIN = ADMIN)
↓
Route accessible: ✅
```

## Testing Role-Based Routing

### Test Cases

1. **Unauthenticated Access**
   - Navigate to `/admin/dashboard` without login
   - Expected: Redirected to `/auth/login`

2. **Wrong Role Access**
   - Login as COMPANY user
   - Navigate to `/admin/dashboard`
   - Expected: Redirected to `/access-denied` or `/company/dashboard`

3. **Correct Role Access**
   - Login as ADMIN user
   - Navigate to `/admin/dashboard`
   - Expected: Route accessible

4. **Direct URL Access**
   - Login as COMPANY user
   - Manually type `/admin/users` in browser
   - Expected: Redirected to `/access-denied`

## Best Practices

1. **Always use both guards together**:
   ```typescript
   canActivate: [AuthGuard, RoleGuard]
   ```

2. **Specify roles in route data**:
   ```typescript
   data: { roles: ['ADMIN'] }
   ```

3. **Use lazy loading for feature modules**:
   ```typescript
   loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule)
   ```

4. **Provide access denied page** for better UX

5. **Store returnUrl** for redirect after login

## Security Notes

⚠️ **Important**: Client-side route guards are for UX only. They do NOT provide security.

- Always validate roles on the backend
- Never trust client-side role checks
- Backend API should verify JWT token and user role
- Client-side guards prevent accidental navigation, not malicious access

## Summary

- ✅ **AuthGuard**: Checks authentication
- ✅ **RoleGuard**: Checks role
- ✅ **Route Protection**: `/admin/**` → ADMIN only, `/company/**` → COMPANY only
- ✅ **Redirects**: Login page for unauthenticated, access-denied for role mismatch
- ✅ **Access Denied Page**: User-friendly error page with navigation options

