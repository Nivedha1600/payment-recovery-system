# Angular Project Structure Diagram

## Visual Structure Overview

```
payment-recovery-system-ui/
│
├── src/app/
│   │
│   ├── core/                          ════════════════════════════╗
│   │   ├── guards/                   │ Singleton Services         │
│   │   │   ├── auth.guard.ts        │ Loaded Once at Startup    │
│   │   │   └── role.guard.ts        │                            │
│   │   ├── interceptors/             │                            │
│   │   │   ├── auth.interceptor.ts  │                            │
│   │   │   └── error.interceptor.ts │                            │
│   │   ├── services/                 │                            │
│   │   │   ├── auth.service.ts      │                            │
│   │   │   └── token.service.ts     │                            │
│   │   └── models/                   │                            │
│   │       └── user.model.ts         ╚═══════════════════════════╝
│   │
│   ├── shared/                        ════════════════════════════╗
│   │   ├── components/               │ Reusable Components       │
│   │   │   ├── header/              │ Imported by Features      │
│   │   │   ├── sidebar/             │                            │
│   │   │   └── data-table/          │                            │
│   │   ├── pipes/                    │                            │
│   │   └── directives/               ╚═══════════════════════════╝
│   │
│   ├── auth/                          ════════════════════════════╗
│   │   ├── components/               │ Authentication Module      │
│   │   │   ├── login/               │ Shared by Both Roles      │
│   │   │   └── register/            │                            │
│   │   └── auth.module.ts            ╚═══════════════════════════╝
│   │
│   ├── admin/                         ════════════════════════════╗
│   │   ├── components/               │ ADMIN Role Module          │
│   │   │   ├── dashboard/          │ Lazy-Loaded                │
│   │   │   ├── companies/          │ Protected by RoleGuard     │
│   │   │   └── users/              │                            │
│   │   ├── services/                 │                            │
│   │   └── admin.module.ts           ╚═══════════════════════════╝
│   │
│   ├── company/                       ════════════════════════════╗
│   │   ├── components/               │ COMPANY Role Module        │
│   │   │   ├── dashboard/           │ Lazy-Loaded                │
│   │   │   ├── invoices/            │ Protected by RoleGuard     │
│   │   │   ├── customers/          │                            │
│   │   │   └── payments/           │                            │
│   │   ├── services/                 │                            │
│   │   └── company.module.ts         ╚═══════════════════════════╝
│   │
│   ├── layout/                         ════════════════════════════╗
│   │   ├── admin-layout/             │ Layout Components          │
│   │   ├── company-layout/          │ Role-Specific Shells       │
│   │   └── auth-layout/             ╚═══════════════════════════╝
│   │
│   ├── app-routing.module.ts          ════════════════════════════╗
│   │                                │ Root Routing               │
│   │                                │ Role-Based Guards          │
│   │                                │ Lazy Loading               │
│   └── app.module.ts                 ╚═══════════════════════════╝
│
└── src/assets/
    ├── styles/
    └── images/
```

## Module Dependency Flow

```
┌─────────────────────────────────────────────────────────────┐
│                        App Module                            │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │   Core   │  │  Shared  │  │   Auth   │  │  Layout   │  │
│  │  Module  │  │  Module  │  │  Module  │  │ Components│  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  │
└─────────────────────────────────────────────────────────────┘
         │              │              │              │
         │              │              │              │
         ▼              ▼              ▼              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Feature Modules                          │
│  ┌──────────────────────┐  ┌──────────────────────┐        │
│  │    Admin Module      │  │   Company Module     │        │
│  │  (Lazy-Loaded)       │  │  (Lazy-Loaded)        │        │
│  │                      │  │                      │        │
│  │  ┌────────────────┐ │  │  ┌────────────────┐ │        │
│  │  │ Uses: Shared   │ │  │  │ Uses: Shared   │ │        │
│  │  │ Uses: Core     │ │  │  │ Uses: Core     │ │        │
│  │  │ Uses: Layout   │ │  │  │ Uses: Layout   │ │        │
│  │  └────────────────┘ │  │  └────────────────┘ │        │
│  └──────────────────────┘  └──────────────────────┘        │
└─────────────────────────────────────────────────────────────┘
```

## Route Flow Diagram

```
                    ┌─────────────┐
                    │   App Root   │
                    └──────┬───────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
   ┌─────────┐       ┌─────────┐       ┌─────────┐
   │  /auth   │       │ /admin  │       │/company  │
   │          │       │         │       │          │
   │ No Auth  │       │ ADMIN   │       │ COMPANY  │
   │ Required │       │ Role    │       │ Role     │
   └────┬─────┘       └────┬────┘       └────┬─────┘
        │                 │                  │
        ▼                 ▼                  ▼
   ┌─────────┐      ┌──────────┐      ┌──────────┐
   │  Login   │      │ Dashboard│      │ Dashboard│
   │ Register │      │ Companies│      │ Invoices │
   │ Forgot   │      │ Users    │      │ Customers│
   │ Password │      │ Settings │      │ Payments │
   └──────────┘      └──────────┘      └──────────┘
```

## Role-Based Access Matrix

| Feature              | ADMIN | COMPANY | Auth Required |
|----------------------|-------|---------|---------------|
| Login/Logout         | ✅    | ✅      | ❌            |
| Admin Dashboard      | ✅    | ❌      | ✅            |
| Company Management   | ✅    | ❌      | ✅            |
| User Management      | ✅    | ❌      | ✅            |
| Company Dashboard    | ❌    | ✅      | ✅            |
| Invoice Management   | ❌    | ✅      | ✅            |
| Customer Management  | ❌    | ✅      | ✅            |
| Payment Tracking    | ❌    | ✅      | ✅            |

## Component Hierarchy

### Admin Layout
```
AdminLayoutComponent
├── AdminHeaderComponent (from shared)
├── AdminSidebarComponent (from shared)
└── Router Outlet
    ├── AdminDashboardComponent
    ├── CompanyListComponent
    ├── CompanyDetailComponent
    ├── UserListComponent
    └── SystemSettingsComponent
```

### Company Layout
```
CompanyLayoutComponent
├── CompanyHeaderComponent (from shared)
├── CompanySidebarComponent (from shared)
└── Router Outlet
    ├── CompanyDashboardComponent
    ├── InvoiceListComponent
    ├── InvoiceDetailComponent
    ├── CustomerListComponent
    ├── PaymentListComponent
    └── CompanySettingsComponent
```

### Auth Layout
```
AuthLayoutComponent
└── Router Outlet
    ├── LoginComponent
    ├── RegisterComponent
    └── ForgotPasswordComponent
```

## Service Dependency Graph

```
┌─────────────────────────────────────────┐
│         Core Services (Singleton)       │
├─────────────────────────────────────────┤
│  AuthService                             │
│    ├── TokenService                     │
│    └── CompanyService                   │
│                                         │
│  ApiService (Base HTTP)                 │
└─────────────────────────────────────────┘
         │                    │
         │                    │
         ▼                    ▼
┌─────────────────┐  ┌─────────────────┐
│  Admin Services │  │ Company Services │
├─────────────────┤  ├─────────────────┤
│ CompanyMgmtSvc  │  │ InvoiceService  │
│ UserMgmtSvc     │  │ CustomerService │
│ AdminApiService │  │ PaymentService  │
└─────────────────┘  └─────────────────┘
```

## File Organization Summary

### By Type
- **Components**: Feature-specific UI components
- **Services**: Business logic and API calls
- **Models**: TypeScript interfaces/types
- **Guards**: Route protection
- **Interceptors**: HTTP request/response handling
- **Pipes**: Data transformation
- **Directives**: DOM manipulation

### By Module
- **Core**: Application-wide singletons
- **Shared**: Reusable across features
- **Auth**: Authentication flow
- **Admin**: Admin-only features
- **Company**: Company user features
- **Layout**: Application shells

## Key Design Decisions

1. **Role Separation**: Complete module separation for ADMIN and COMPANY roles
2. **Lazy Loading**: Feature modules loaded on-demand for better performance
3. **Shared Resources**: Common components in shared module to avoid duplication
4. **Core Services**: Singleton services for application-wide state
5. **Layout Components**: Role-specific layouts for better UX
6. **Route Guards**: Multi-layer protection (Auth + Role guards)

