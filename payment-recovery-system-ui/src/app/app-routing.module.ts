import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { RoleGuard } from './core/guards/role.guard';

/**
 * Root routing module
 * Configures top-level routes with role-based guards
 * 
 * Guard Order:
 * 1. AuthGuard - Checks if user is authenticated
 * 2. RoleGuard - Checks if user has required role
 */
const routes: Routes = [
  // Auth routes (no authentication required)
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule)
  },
  
  // Admin routes (requires ADMIN role)
  // All routes under /admin/** are protected
  {
    path: 'admin',
    canActivate: [AuthGuard, RoleGuard], // Both guards must pass
    data: { roles: ['ADMIN'] }, // Required role
    loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule)
  },
  
  // Company routes (requires COMPANY role)
  // All routes under /company/** are protected
  {
    path: 'company',
    canActivate: [AuthGuard, RoleGuard], // Both guards must pass
    data: { roles: ['COMPANY'] }, // Required role
    loadChildren: () => import('./company/company.module').then(m => m.CompanyModule)
  },
  
  // Access Denied page (for better UX when role mismatch occurs)
  {
    path: 'access-denied',
    loadChildren: () => import('./shared/components/access-denied/access-denied.module').then(m => m.AccessDeniedModule)
  },
  
  // Default redirect to login
  { path: '', redirectTo: '/auth/login', pathMatch: 'full' },
  
  // Wildcard route - redirect to login
  { path: '**', redirectTo: '/auth/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

