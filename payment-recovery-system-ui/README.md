# Payment Recovery System - Angular Frontend

Angular frontend application for the B2B Payment Recovery System with multi-role support (ADMIN and COMPANY).

## Project Setup

### Create Angular Project

```bash
ng new payment-recovery-system-ui \
  --routing=true \
  --style=scss \
  --strict=true \
  --skip-git=false \
  --package-manager=npm
```

### Prerequisites

- Node.js (v18.x or higher)
- npm (v9.x or higher) or yarn
- Angular CLI (v18.x or higher)

## Project Structure

This project follows a **role-based modular architecture** designed for scalability and maintainability.

```
payment-recovery-system-ui/
├── src/
│   ├── app/
│   │   ├── core/                          # Core Module - Singleton Services
│   │   │   ├── guards/
│   │   │   │   ├── auth.guard.ts
│   │   │   │   ├── role.guard.ts
│   │   │   │   └── index.ts
│   │   │   ├── interceptors/
│   │   │   │   ├── auth.interceptor.ts
│   │   │   │   ├── error.interceptor.ts
│   │   │   │   ├── loading.interceptor.ts
│   │   │   │   └── index.ts
│   │   │   ├── services/
│   │   │   │   ├── auth.service.ts
│   │   │   │   ├── token.service.ts
│   │   │   │   ├── company.service.ts
│   │   │   │   ├── api.service.ts
│   │   │   │   └── index.ts
│   │   │   ├── models/
│   │   │   │   ├── user.model.ts
│   │   │   │   ├── company.model.ts
│   │   │   │   ├── role.model.ts
│   │   │   │   └── index.ts
│   │   │   └── core.module.ts
│   │   │
│   │   ├── shared/                         # Shared Module - Reusable Components
│   │   │   ├── components/
│   │   │   │   ├── header/
│   │   │   │   ├── sidebar/
│   │   │   │   ├── loading-spinner/
│   │   │   │   ├── confirm-dialog/
│   │   │   │   ├── data-table/
│   │   │   │   ├── pagination/
│   │   │   │   └── index.ts
│   │   │   ├── directives/
│   │   │   │   ├── click-outside.directive.ts
│   │   │   │   └── index.ts
│   │   │   ├── pipes/
│   │   │   │   ├── currency.pipe.ts
│   │   │   │   ├── date-format.pipe.ts
│   │   │   │   └── index.ts
│   │   │   ├── validators/
│   │   │   │   ├── custom.validators.ts
│   │   │   │   └── index.ts
│   │   │   └── shared.module.ts
│   │   │
│   │   ├── auth/                           # Authentication Module
│   │   │   ├── components/
│   │   │   │   ├── login/
│   │   │   │   │   ├── login.component.ts
│   │   │   │   │   ├── login.component.html
│   │   │   │   │   ├── login.component.scss
│   │   │   │   │   └── login.component.spec.ts
│   │   │   │   ├── register/
│   │   │   │   ├── forgot-password/
│   │   │   │   └── reset-password/
│   │   │   ├── services/
│   │   │   │   └── auth-api.service.ts
│   │   │   ├── auth-routing.module.ts
│   │   │   └── auth.module.ts
│   │   │
│   │   ├── admin/                          # Admin Module - ADMIN Role Only
│   │   │   ├── components/
│   │   │   │   ├── dashboard/
│   │   │   │   │   ├── dashboard.component.ts
│   │   │   │   │   ├── dashboard.component.html
│   │   │   │   │   └── dashboard.component.scss
│   │   │   │   ├── companies/
│   │   │   │   │   ├── company-list/
│   │   │   │   │   ├── company-detail/
│   │   │   │   │   ├── company-form/
│   │   │   │   │   └── index.ts
│   │   │   │   ├── users/
│   │   │   │   │   ├── user-list/
│   │   │   │   │   ├── user-detail/
│   │   │   │   │   └── user-form/
│   │   │   │   ├── system-settings/
│   │   │   │   └── reports/
│   │   │   ├── services/
│   │   │   │   ├── company-management.service.ts
│   │   │   │   ├── user-management.service.ts
│   │   │   │   └── admin-api.service.ts
│   │   │   ├── models/
│   │   │   │   ├── admin-company.model.ts
│   │   │   │   └── admin-user.model.ts
│   │   │   ├── admin-routing.module.ts
│   │   │   └── admin.module.ts
│   │   │
│   │   ├── company/                        # Company Module - COMPANY Role Only
│   │   │   ├── components/
│   │   │   │   ├── dashboard/
│   │   │   │   │   ├── dashboard.component.ts
│   │   │   │   │   ├── dashboard.component.html
│   │   │   │   │   └── dashboard.component.scss
│   │   │   │   ├── invoices/
│   │   │   │   │   ├── invoice-list/
│   │   │   │   │   ├── invoice-detail/
│   │   │   │   │   ├── invoice-upload/
│   │   │   │   │   ├── invoice-form/
│   │   │   │   │   └── index.ts
│   │   │   │   ├── customers/
│   │   │   │   │   ├── customer-list/
│   │   │   │   │   ├── customer-detail/
│   │   │   │   │   └── customer-form/
│   │   │   │   ├── payments/
│   │   │   │   │   ├── payment-list/
│   │   │   │   │   ├── payment-detail/
│   │   │   │   │   └── payment-form/
│   │   │   │   ├── reminders/
│   │   │   │   │   ├── reminder-list/
│   │   │   │   │   └── reminder-history/
│   │   │   │   └── settings/
│   │   │   │       ├── company-profile/
│   │   │   │       └── user-settings/
│   │   │   ├── services/
│   │   │   │   ├── invoice.service.ts
│   │   │   │   ├── customer.service.ts
│   │   │   │   ├── payment.service.ts
│   │   │   │   └── company-api.service.ts
│   │   │   ├── models/
│   │   │   │   ├── invoice.model.ts
│   │   │   │   ├── customer.model.ts
│   │   │   │   └── payment.model.ts
│   │   │   ├── company-routing.module.ts
│   │   │   └── company.module.ts
│   │   │
│   │   ├── layout/                         # Layout Components
│   │   │   ├── admin-layout/
│   │   │   │   ├── admin-layout.component.ts
│   │   │   │   ├── admin-layout.component.html
│   │   │   │   └── admin-layout.component.scss
│   │   │   ├── company-layout/
│   │   │   │   ├── company-layout.component.ts
│   │   │   │   ├── company-layout.component.html
│   │   │   │   └── company-layout.component.scss
│   │   │   └── auth-layout/
│   │   │       ├── auth-layout.component.ts
│   │   │       ├── auth-layout.component.html
│   │   │       └── auth-layout.component.scss
│   │   │
│   │   ├── app-routing.module.ts           # Root routing with role-based guards
│   │   ├── app.component.ts
│   │   ├── app.component.html
│   │   ├── app.component.scss
│   │   └── app.module.ts
│   │
│   ├── assets/
│   │   ├── images/
│   │   ├── icons/
│   │   ├── fonts/
│   │   └── styles/
│   │       ├── _variables.scss
│   │       ├── _mixins.scss
│   │       ├── _base.scss
│   │       └── _utilities.scss
│   │
│   ├── environments/
│   │   ├── environment.ts
│   │   └── environment.prod.ts
│   │
│   ├── styles.scss
│   ├── main.ts
│   └── index.html
│
├── .angular/
├── node_modules/
├── .editorconfig
├── .gitignore
├── angular.json
├── package.json
├── tsconfig.json
├── tsconfig.app.json
├── tsconfig.spec.json
└── README.md
```

## Key Features

### Role-Based Access Control (RBAC)

- **ADMIN Role**: Full system access, company management, user management
- **COMPANY Role**: Company-specific data access (invoices, customers, payments)

### Module Organization

1. **Core Module**: Singleton services, guards, interceptors (loaded once)
2. **Shared Module**: Reusable components, pipes, directives
3. **Auth Module**: Authentication and authorization (login, register, password reset)
4. **Admin Module**: Admin-only features (lazy-loaded)
5. **Company Module**: Company user features (lazy-loaded)

### Security

- Route guards for role-based access
- HTTP interceptors for authentication
- Token management
- Role-based component visibility

## Development

### Start Development Server

```bash
ng serve
# or
ng serve --open
```

### Build for Production

```bash
ng build --configuration production
```

### Run Tests

```bash
ng test
```

## Environment Configuration

Update `src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  appName: 'Payment Recovery System',
  version: '1.0.0'
};
```

## Next Steps

1. Set up authentication service and guards
2. Configure HTTP interceptors
3. Create role-based routing
4. Implement layout components
5. Build admin and company feature modules
6. Add global styles and theme

