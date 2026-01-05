import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CompanyApiService } from '../../services/company-api.service';
import { Invoice } from '../../models/invoice.model';

/**
 * Invoice Detail Component
 * Displays detailed information about a single invoice
 */
@Component({
  selector: 'app-invoice-detail',
  templateUrl: './invoice-detail.component.html',
  styleUrls: ['./invoice-detail.component.scss']
})
export class InvoiceDetailComponent implements OnInit {
  invoice: Invoice | null = null;
  invoiceId: number | null = null;
  isLoading = false;
  errorMessage: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private companyApiService: CompanyApiService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.invoiceId = +params['id'];
      if (this.invoiceId) {
        this.loadInvoice();
      }
    });
  }

  /**
   * Load invoice details
   */
  loadInvoice(): void {
    if (!this.invoiceId) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    this.companyApiService.getInvoiceById(this.invoiceId)
      .subscribe({
        next: (invoice) => {
          this.invoice = invoice;
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = 'Failed to load invoice details. Please try again.';
          this.isLoading = false;
          console.error('Error loading invoice:', error);
        }
      });
  }

  /**
   * Go back to invoices list
   */
  goBack(): void {
    this.router.navigate(['/company/invoices']);
  }

  /**
   * Mark invoice as paid
   */
  markAsPaid(): void {
    if (!this.invoice || this.invoice.status !== 'PENDING') {
      return;
    }

    if (!this.invoice.amount || this.invoice.amount <= 0) {
      this.errorMessage = 'Cannot mark invoice as paid: Invalid amount.';
      return;
    }

    const confirmMessage = `Are you sure you want to mark invoice ${this.invoice.invoiceNumber || 'N/A'} as paid?`;
    if (!confirm(confirmMessage)) {
      return;
    }

    const today = new Date().toISOString().split('T')[0];

    this.companyApiService.markInvoiceAsPaid(this.invoice.id, this.invoice.amount, today)
      .subscribe({
        next: () => {
          // Reload invoice after successful payment
          this.loadInvoice();
        },
        error: (error) => {
          this.errorMessage = 'Failed to mark invoice as paid. Please try again.';
          console.error('Error marking invoice as paid:', error);
        }
      });
  }

  /**
   * Format currency
   */
  formatCurrency(value: number | null | undefined): string {
    if (value === null || value === undefined || isNaN(value)) {
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
  formatDate(dateString: string | null | undefined): string {
    if (!dateString) {
      return 'N/A';
    }
    try {
      const date = new Date(dateString);
      if (isNaN(date.getTime())) {
        return 'N/A';
      }
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    } catch (error) {
      return 'N/A';
    }
  }

  /**
   * Get status badge class
   */
  getStatusClass(status: string): string {
    switch (status) {
      case 'DRAFT':
        return 'status-draft';
      case 'PENDING':
        return 'status-active';
      case 'PAID':
        return 'status-paid';
      case 'PARTIAL':
        return 'status-partial';
      default:
        return 'status-default';
    }
  }

  /**
   * Get status label
   */
  getStatusLabel(status: string): string {
    switch (status) {
      case 'DRAFT':
        return 'DRAFT';
      case 'PENDING':
        return 'ACTIVE';
      case 'PAID':
        return 'PAID';
      case 'PARTIAL':
        return 'PARTIAL';
      default:
        return status;
    }
  }

  /**
   * Check if invoice can be marked as paid
   */
  canMarkAsPaid(): boolean {
    return this.invoice?.status === 'PENDING' && !!this.invoice?.amount && this.invoice.amount > 0;
  }
}

