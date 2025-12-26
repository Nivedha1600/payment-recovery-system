# Quick Reference Guide

## Angular CLI Commands

### Project Creation
```bash
ng new payment-recovery-system-ui --routing=true --style=scss --strict=true
```

### Generate Components
```bash
# Core module
ng generate component core/components/example --module=core

# Shared module
ng generate component shared/components/example --module=shared

# Auth module
ng generate component auth/components/login --module=auth

# Admin module
ng generate component admin/components/dashboard --module=admin

# Company module
ng generate component company/components/invoice-list --module=company
```

### Generate Services
```bash
# Core service
ng generate service core/services/example

# Feature service
ng generate service admin/services/company-management
ng generate service company/services/invoice
```

### Generate Guards
```bash
ng generate guard core/guards/auth
ng generate guard core/guards/role
```

### Generate Interceptors
```bash
ng generate interceptor core/interceptors/auth
ng generate interceptor core/interceptors/error
```

## Path Aliases

```typescript
// Core
import { AuthService } from '@core/services/auth.service';
import { AuthGuard } from '@core/guards/auth.guard';

// Shared
import { DataTableComponent } from '@shared/components/data-table';

// Features
import { InvoiceService } from '@company/services/invoice.service';
import { CompanyListComponent } from '@admin/components/company-list';

// Layout
import { AdminLayoutComponent } from '@layout/admin-layout';

// Environment
import { environment } from '@env/environment';
```

## Common Patterns

### Role-Based Component Visibility
```typescript
// In component
userRole$ = this.authService.currentUser$.pipe(
  map(user => user?.role)
);

// In template
<div *ngIf="userRole$ | async === 'ADMIN'">
  Admin Only Content
</div>
```

### Role-Based Route Protection
```typescript
{
  path: 'admin',
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: ['ADMIN'] },
  loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule)
}
```

### Service Injection
```typescript
@Injectable({ providedIn: 'root' })
export class InvoiceService {
  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}
}
```

### HTTP Interceptor
```typescript
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.tokenService.getToken();
    if (token) {
      req = req.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
    }
    return next.handle(req);
  }
}
```

## Module Imports

### Core Module (AppModule)
```typescript
@NgModule({
  imports: [
    CoreModule,  // Import once
    AppRoutingModule
  ]
})
```

### Feature Module (Admin/Company)
```typescript
@NgModule({
  imports: [
    SharedModule,  // Import reusable components
    FeatureRoutingModule
  ]
})
```

## Routing Examples

### Root Routes
```typescript
const routes: Routes = [
  { path: 'auth', loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule) },
  { 
    path: 'admin', 
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN'] },
    loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule) 
  },
  { 
    path: 'company', 
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['COMPANY'] },
    loadChildren: () => import('./company/company.module').then(m => m.CompanyModule) 
  }
];
```

### Feature Routes (Admin)
```typescript
const routes: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'companies', component: CompanyListComponent },
      { path: 'companies/:id', component: CompanyDetailComponent }
    ]
  }
];
```

## Environment Configuration

### Development
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

### Production
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.paymentrecovery.com/api'
};
```

## Common File Templates

### Component Template
```typescript
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-example',
  templateUrl: './example.component.html',
  styleUrls: ['./example.component.scss']
})
export class ExampleComponent implements OnInit {
  constructor() {}

  ngOnInit(): void {}
}
```

### Service Template
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';

@Injectable({ providedIn: 'root' })
export class ExampleService {
  private apiUrl = `${environment.apiUrl}/example`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }
}
```

### Guard Template
```typescript
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '@core/services/auth.service';

@Injectable({ providedIn: 'root' })
export class ExampleGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    // Guard logic
    return true;
  }
}
```

## Testing Commands

```bash
# Unit tests
ng test

# E2E tests
ng e2e

# Build
ng build

# Production build
ng build --configuration production

# Lint
ng lint
```

## Useful Angular CLI Options

```bash
# Skip tests
ng generate component example --skip-tests

# Inline template/style
ng generate component example --inline-template --inline-style

# Dry run (preview changes)
ng generate component example --dry-run
```

## Troubleshooting

### Module Not Found
- Check path aliases in `tsconfig.json`
- Verify module is imported in parent module
- Check file paths are correct

### Route Not Working
- Verify route is defined in routing module
- Check guards are not blocking
- Verify lazy loading syntax

### Service Injection Error
- Ensure service has `@Injectable({ providedIn: 'root' })`
- Check service is imported in module if not root
- Verify service is not circular dependency

### Build Errors
- Run `ng build` to see detailed errors
- Check TypeScript strict mode settings
- Verify all imports are correct

