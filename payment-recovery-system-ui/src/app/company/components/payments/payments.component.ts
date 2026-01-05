import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CompanyApiService } from '../../services/company-api.service';
import { Payment, PaymentListResponse } from '../../models/payment.model';
import { Invoice, InvoiceListResponse } from '../../models/invoice.model';

/**
 * Payments Component
 * Displays payment records and allows marking invoices as paid
 */
@Component({
  selector: 'app-payments',
  templateUrl: './payments.component.html',
  styleUrls: ['./payments.component.scss']
})
export class PaymentsComponent implements OnInit {
  // Payment records (PAID invoices)
  payments: Payment[] = [];
  totalPayments = 0;
  currentPage = 0;
  pageSize = 10;
  isLoading = false;
  errorMessage: string | null = null;

  // Pending invoices (can be marked as paid)
  pendingInvoices: Invoice[] = [];
  showPendingSection = true;
  isLoadingPending = false;

  // Mark as paid form
  selectedInvoice: Invoice | null = null;
  markPaidForm = {
    amountReceived: null as number | null,
    paymentDate: new Date().toISOString().split('T')[0] // Today's date
  };
  isMarkingPaid = false;

  constructor(
    private companyApiService: CompanyApiService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPayments();
    this.loadPendingInvoices();
  }

  /**
   * Go back to dashboard
   */
  goBack(): void {
    this.router.navigate(['/company/dashboard']);
  }

  /**
   * Load payment records
   */
  loadPayments(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.companyApiService.getPayments(this.currentPage, this.pageSize)
      .subscribe({
        next: (response: PaymentListResponse) => {
          this.payments = response.payments;
          this.totalPayments = response.total;
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = 'Failed to load payments. Please try again.';
          this.isLoading = false;
          console.error('Error loading payments:', error);
        }
      });
  }

  /**
   * Load pending invoices (for marking as paid)
   */
  loadPendingInvoices(): void {
    this.isLoadingPending = true;

    this.companyApiService.getInvoices(0, 5, 'PENDING')
      .subscribe({
        next: (response: InvoiceListResponse) => {
          this.pendingInvoices = response.invoices;
          this.isLoadingPending = false;
        },
        error: (error) => {
          console.error('Error loading pending invoices:', error);
          this.isLoadingPending = false;
        }
      });
  }

  /**
   * Open mark as paid form for an invoice
   */
  openMarkAsPaidForm(invoice: Invoice): void {
    this.selectedInvoice = invoice;
    this.markPaidForm = {
      amountReceived: invoice.amount,
      paymentDate: new Date().toISOString().split('T')[0]
    };
  }

  /**
   * Close mark as paid form
   */
  closeMarkAsPaidForm(): void {
    this.selectedInvoice = null;
    this.markPaidForm = {
      amountReceived: null,
      paymentDate: new Date().toISOString().split('T')[0]
    };
  }

  /**
   * Mark invoice as paid
   */
  markInvoiceAsPaid(): void {
    if (!this.selectedInvoice) {
      return;
    }

    if (!this.markPaidForm.amountReceived || this.markPaidForm.amountReceived <= 0) {
      this.errorMessage = 'Please enter a valid amount.';
      return;
    }

    if (!this.markPaidForm.paymentDate) {
      this.errorMessage = 'Please select a payment date.';
      return;
    }

    this.isMarkingPaid = true;
    this.errorMessage = null;

    this.companyApiService.markInvoiceAsPaid(
      this.selectedInvoice.id,
      this.markPaidForm.amountReceived,
      this.markPaidForm.paymentDate
    ).subscribe({
      next: () => {
        this.isMarkingPaid = false;
        this.closeMarkAsPaidForm();
        
        // Reload both payments and pending invoices
        this.loadPayments();
        this.loadPendingInvoices();
      },
      error: (error) => {
        this.isMarkingPaid = false;
        this.errorMessage = error.error?.message || 'Failed to mark invoice as paid. Please try again.';
        console.error('Error marking invoice as paid:', error);
      }
    });
  }

  /**
   * Format currency
   */
  formatCurrency(value: number | null | undefined): string {
    if (value === null || value === undefined) {
      return '$0.00';
    }
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(value);
  }

  /**
   * Format date
   */
  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  /**
   * Go to next page
   */
  nextPage(): void {
    if (this.hasNextPage()) {
      this.currentPage++;
      this.loadPayments();
    }
  }

  /**
   * Go to previous page
   */
  previousPage(): void {
    if (this.hasPreviousPage()) {
      this.currentPage--;
      this.loadPayments();
    }
  }

  /**
   * Check if there is a next page
   */
  hasNextPage(): boolean {
    return (this.currentPage + 1) * this.pageSize < this.totalPayments;
  }

  /**
   * Check if there is a previous page
   */
  hasPreviousPage(): boolean {
    return this.currentPage > 0;
  }

  /**
   * Get total pages
   */
  getTotalPages(): number {
    return Math.ceil(this.totalPayments / this.pageSize);
  }

  /**
   * Expose Math to template
   */
  Math = Math;
}

