# Angular Setup Guide - Multi-Role SaaS Application

## Step-by-Step Setup

### 1. Create Angular Project

```bash
ng new payment-recovery-system-ui \
  --routing=true \
  --style=scss \
  --strict=true \
  --skip-git=false \
  --package-manager=npm
```

### 2. Navigate to Project

```bash
cd payment-recovery-system-ui
```

### 3. Create Folder Structure

Create the following directory structure:

```bash
# Core module
mkdir -p src/app/core/guards
mkdir -p src/app/core/interceptors
mkdir -p src/app/core/services
mkdir -p src/app/core/models

# Shared module
mkdir -p src/app/shared/components
mkdir -p src/app/shared/directives
mkdir -p src/app/shared/pipes
mkdir -p src/app/shared/validators

# Auth module
mkdir -p src/app/auth/components
mkdir -p src/app/auth/services

# Admin module
mkdir -p src/app/admin/components
mkdir -p src/app/admin/services
mkdir -p src/app/admin/models

# Company module
mkdir -p src/app/company/components
mkdir -p src/app/company/services
mkdir -p src/app/company/models

# Layout module
mkdir -p src/app/layout
```

### 4. Install Dependencies

```bash
# UI Component Library (choose one)
npm install @angular/material @angular/cdk @angular/animations
# OR
npm install primeng primeicons
# OR
npm install ng-bootstrap bootstrap

# Utilities
npm install lodash-es @types/lodash-es
npm install date-fns
```

### 5. Configure Path Aliases

Update `tsconfig.json`:

```json
{
  "compilerOptions": {
    "baseUrl": "./",
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

### 6. Configure Environment

Update `src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  appName: 'Payment Recovery System',
  version: '1.0.0',
  roles: {
    ADMIN: 'ADMIN',
    COMPANY: 'COMPANY'
  }
};
```

### 7. Create Core Models

**`src/app/core/models/role.model.ts`**:

```typescript
export enum UserRole {
  ADMIN = 'ADMIN',
  COMPANY = 'COMPANY'
}
```

**`src/app/core/models/user.model.ts`**:

```typescript
import { UserRole } from './role.model';

export interface User {
  id: number;
  username: string;
  email: string;
  role: UserRole;
  companyId?: number;
  companyName?: string;
  isActive: boolean;
}
```

### 8. Create Core Services

**`src/app/core/services/auth.service.ts`**:

```typescript
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  setCurrentUser(user: User): void {
    this.currentUserSubject.next(user);
  }

  logout(): void {
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    return this.currentUserSubject.value !== null;
  }

  hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    return user?.role === role;
  }
}
```

### 9. Create Guards

**`src/app/core/guards/auth.guard.ts`**:

```typescript
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    if (this.authService.isAuthenticated()) {
      return true;
    }
    this.router.navigate(['/auth/login']);
    return false;
  }
}
```

**`src/app/core/guards/role.guard.ts`**:

```typescript
import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const requiredRoles = route.data['roles'] as string[];
    const user = this.authService.getCurrentUser();

    if (!user) {
      this.router.navigate(['/auth/login']);
      return false;
    }

    if (!requiredRoles || requiredRoles.includes(user.role)) {
      return true;
    }

    // Redirect based on user role
    if (user.role === 'ADMIN') {
      this.router.navigate(['/admin/dashboard']);
    } else if (user.role === 'COMPANY') {
      this.router.navigate(['/company/dashboard']);
    } else {
      this.router.navigate(['/auth/login']);
    }

    return false;
  }
}
```

### 10. Configure Root Routing

**`src/app/app-routing.module.ts`**:

```typescript
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@core/guards/auth.guard';
import { RoleGuard } from '@core/guards/role.guard';
import { AuthLayoutComponent } from '@layout/auth-layout/auth-layout.component';
import { AdminLayoutComponent } from '@layout/admin-layout/admin-layout.component';
import { CompanyLayoutComponent } from '@layout/company-layout/company-layout.component';

const routes: Routes = [
  {
    path: 'auth',
    component: AuthLayoutComponent,
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN'] },
    loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule)
  },
  {
    path: 'company',
    component: CompanyLayoutComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['COMPANY'] },
    loadChildren: () => import('./company/company.module').then(m => m.CompanyModule)
  },
  { path: '', redirectTo: '/auth/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/auth/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
```

### 11. Create Module Files

Create empty module files for each feature:

- `src/app/core/core.module.ts`
- `src/app/shared/shared.module.ts`
- `src/app/auth/auth.module.ts`
- `src/app/admin/admin.module.ts`
- `src/app/company/company.module.ts`

### 12. Create Layout Components

Generate layout components:

```bash
ng generate component layout/auth-layout
ng generate component layout/admin-layout
ng generate component layout/company-layout
```

### 13. Start Development

```bash
ng serve
```

## Next Steps

1. Implement authentication service with API integration
2. Create login component
3. Build admin dashboard
4. Build company dashboard
5. Implement feature modules
6. Add global styles and theme
7. Configure HTTP interceptors

## File Naming Conventions

- **Components**: `kebab-case.component.ts`
- **Services**: `kebab-case.service.ts`
- **Models**: `kebab-case.model.ts`
- **Guards**: `kebab-case.guard.ts`
- **Interceptors**: `kebab-case.interceptor.ts`

## Module Import Strategy

1. **Core Module**: Import once in `AppModule`
2. **Shared Module**: Import in feature modules as needed
3. **Feature Modules**: Lazy-loaded via routing

