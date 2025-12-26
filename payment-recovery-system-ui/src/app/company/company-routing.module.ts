import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CompanyDashboardComponent } from './components/dashboard/dashboard.component';
import { CustomersComponent } from './components/customers/customers.component';
import { InvoicesComponent } from './components/invoices/invoices.component';
import { DraftInvoicesComponent } from './components/draft-invoices/draft-invoices.component';
import { DraftInvoiceReviewComponent } from './components/draft-invoice-review/draft-invoice-review.component';
import { PaymentsComponent } from './components/payments/payments.component';
import { InvoiceUploadComponent } from './components/invoice-upload/invoice-upload.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    component: CompanyDashboardComponent
  },
  {
    path: 'customers',
    component: CustomersComponent
  },
  {
    path: 'invoices',
    component: InvoicesComponent
  },
  {
    path: 'invoices/upload',
    component: InvoiceUploadComponent
  },
  {
    path: 'draft-invoices',
    component: DraftInvoicesComponent
  },
  {
    path: 'draft-invoices/:id/review',
    component: DraftInvoiceReviewComponent
  },
  {
    path: 'payments',
    component: PaymentsComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CompanyRoutingModule { }

