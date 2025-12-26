import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminRoutingModule } from './admin-routing.module';
import { SharedModule } from '../shared/shared.module';
import { AdminDashboardComponent } from './components/dashboard/dashboard.component';
import { CompanyManagementComponent } from './components/company-management/company-management.component';

/**
 * Admin module
 * Contains admin-only features
 * Lazy-loaded and protected by RoleGuard
 */
@NgModule({
  declarations: [
    AdminDashboardComponent,
    CompanyManagementComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    AdminRoutingModule
  ]
})
export class AdminModule { }

