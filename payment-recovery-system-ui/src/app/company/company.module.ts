import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CompanyRoutingModule } from './company-routing.module';
import { SharedModule } from '../shared/shared.module';
import { CompanyDashboardComponent } from './components/dashboard/dashboard.component';
import { CustomersComponent } from './components/customers/customers.component';
import { InvoicesComponent } from './components/invoices/invoices.component';
import { DraftInvoicesComponent } from './components/draft-invoices/draft-invoices.component';
import { DraftInvoiceReviewComponent } from './components/draft-invoice-review/draft-invoice-review.component';
import { PaymentsComponent } from './components/payments/payments.component';
import { InvoiceUploadComponent } from './components/invoice-upload/invoice-upload.component';

/**
 * Company module
 * Contains company user features
 * Lazy-loaded and protected by RoleGuard
 * Company users should never see Admin menus
 */
@NgModule({
  declarations: [
    CompanyDashboardComponent,
    CustomersComponent,
    InvoicesComponent,
    DraftInvoicesComponent,
    DraftInvoiceReviewComponent,
    PaymentsComponent,
    InvoiceUploadComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    CompanyRoutingModule
  ]
})
export class CompanyModule { }

