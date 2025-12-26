# Shared Components Guide

## Overview

This guide describes the shared UI components available in the `SharedModule` for use across the application.

## Components

### 1. Sidebar Component

**Selector**: `<app-sidebar>`

**Purpose**: Navigation sidebar with role-based menus

**Features**:
- Shows Admin menus only for ADMIN users
- Shows Company menus only for COMPANY users
- Active route highlighting
- Logout button
- Responsive design

**Admin Menu Items**:
- Dashboard (`/admin/dashboard`)
- Companies (`/admin/companies`)

**Company Menu Items**:
- Dashboard (`/company/dashboard`)
- Invoices (`/company/invoices`)
- Draft Invoices (`/company/draft-invoices`)
- Customers (`/company/customers`)
- Payments (`/company/payments`)

**Usage**:
```html
<app-sidebar></app-sidebar>
```

### 2. Topbar Component

**Selector**: `<app-topbar>`

**Purpose**: Top navigation bar with user info and actions

**Features**:
- Displays current user name and role
- User avatar
- Settings button
- Logout button
- Responsive design

**Usage**:
```html
<app-topbar></app-topbar>
```

### 3. Loading Spinner Component

**Selector**: `<app-loading-spinner>`

**Purpose**: Reusable loading indicator

**Inputs**:
- `size`: 'small' | 'medium' | 'large' (default: 'medium')
- `message`: string - Optional message to display
- `fullScreen`: boolean - Full screen overlay (default: false)

**Usage Examples**:
```html
<!-- Small spinner -->
<app-loading-spinner size="small"></app-loading-spinner>

<!-- Medium spinner with message -->
<app-loading-spinner size="medium" message="Loading data..."></app-loading-spinner>

<!-- Full screen spinner -->
<app-loading-spinner fullScreen="true" message="Please wait..."></app-loading-spinner>
```

### 4. Confirm Dialog Component

**Selector**: `<app-confirm-dialog>`

**Purpose**: Reusable confirmation modal

**Inputs**:
- `title`: string - Dialog title (default: 'Confirm Action')
- `message`: string - Confirmation message
- `confirmText`: string - Confirm button text (default: 'Confirm')
- `cancelText`: string - Cancel button text (default: 'Cancel')
- `confirmButtonClass`: string - Button class (default: 'btn-primary')
- `show`: boolean - Show/hide dialog

**Outputs**:
- `confirm`: EventEmitter - Emitted when confirm is clicked
- `cancel`: EventEmitter - Emitted when cancel is clicked
- `close`: EventEmitter - Emitted when dialog is closed

**Usage Example**:
```typescript
// In component
showConfirmDialog = false;

openConfirmDialog(): void {
  this.showConfirmDialog = true;
}

onConfirm(): void {
  // Handle confirmation
  this.showConfirmDialog = false;
}

onCancel(): void {
  this.showConfirmDialog = false;
}
```

```html
<app-confirm-dialog
  [show]="showConfirmDialog"
  title="Delete Invoice"
  message="Are you sure you want to delete this invoice? This action cannot be undone."
  confirmText="Delete"
  cancelText="Cancel"
  confirmButtonClass="btn-danger"
  (confirm)="onConfirm()"
  (cancel)="onCancel()"
  (close)="onCancel()"
></app-confirm-dialog>
```

## Layout Components

### Admin Layout

**Component**: `AdminLayoutComponent`

**Usage**: Wraps admin pages with sidebar and topbar

**Template**:
```html
<div class="admin-layout">
  <app-sidebar></app-sidebar>
  <app-topbar></app-topbar>
  <main class="main-content">
    <router-outlet></router-outlet>
  </main>
</div>
```

### Company Layout

**Component**: `CompanyLayoutComponent`

**Usage**: Wraps company pages with sidebar and topbar

**Template**:
```html
<div class="company-layout">
  <app-sidebar></app-sidebar>
  <app-topbar></app-topbar>
  <main class="main-content">
    <router-outlet></router-outlet>
  </main>
</div>
```

### Auth Layout

**Component**: `AuthLayoutComponent`

**Usage**: Wraps authentication pages (no sidebar/topbar)

**Template**:
```html
<div class="auth-layout">
  <router-outlet></router-outlet>
</div>
```

## Role-Based Menu Display

The Sidebar component automatically shows the correct menus based on user role:

### ADMIN Users See:
- Dashboard
- Companies

### COMPANY Users See:
- Dashboard
- Invoices
- Draft Invoices
- Customers
- Payments

**Implementation**:
```typescript
// Sidebar checks user role
isAdmin(): boolean {
  return this.currentUserRole === 'ADMIN';
}

isCompany(): boolean {
  return this.currentUserRole === 'COMPANY';
}
```

## Styling

All components use SCSS with:
- Consistent color scheme
- Responsive design
- Smooth transitions
- Modern UI patterns

## Integration

### Import SharedModule

In feature modules:
```typescript
import { SharedModule } from '../shared/shared.module';

@NgModule({
  imports: [SharedModule]
})
```

### Use Components

```html
<!-- In any feature component template -->
<app-loading-spinner *ngIf="isLoading" size="medium"></app-loading-spinner>

<app-confirm-dialog
  [show]="showDeleteConfirm"
  title="Delete Item"
  message="Are you sure?"
  (confirm)="deleteItem()"
  (cancel)="showDeleteConfirm = false"
></app-confirm-dialog>
```

## Best Practices

1. **Always import SharedModule** in feature modules that use shared components
2. **Use loading spinner** for async operations
3. **Use confirm dialog** for destructive actions
4. **Sidebar and Topbar** should be in layout components, not feature components
5. **Role-based menus** are handled automatically by Sidebar component

## Customization

### Adding Menu Items

Edit `sidebar.component.ts`:

```typescript
// Add to adminMenuItems or companyMenuItems array
{
  label: 'New Menu Item',
  icon: 'icon-name',
  route: '/admin/new-route'
}
```

### Customizing Colors

Edit component SCSS files to match your brand colors.

## Summary

✅ **Sidebar** - Role-based navigation  
✅ **Topbar** - User info and actions  
✅ **Loading Spinner** - Reusable loading indicator  
✅ **Confirm Dialog** - Reusable confirmation modal  
✅ **Layout Components** - Admin, Company, Auth layouts  

All components are ready to use and properly integrated with the authentication system.

