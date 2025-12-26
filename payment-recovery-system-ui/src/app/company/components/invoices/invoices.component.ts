import { Component, OnInit } from '@angular/core';
import { CompanyApiService } from '../../services/company-api.service';
import { Invoice, InvoiceListResponse } from '../../models/invoice.model';

/**
 * Invoices Component
 * Displays list of invoices with filtering and actions
 */
@Component({
  selector: 'app-invoices',
  templateUrl: './invoices.component.html',
  styleUrls: ['./invoices.component.scss']
})
export class InvoicesComponent implements OnInit {
  invoices: Invoice[] = [];
  totalInvoices = 0;
  currentPage = 0;
  pageSize = 10;
  selectedStatus: string = 'ALL';
  isLoading = false;
  errorMessage: string | null = null;

  statusOptions = [
    { value: 'ALL', label: 'All Statuses' },
    { value: 'DRAFT', label: 'Draft' },
    { value: 'PENDING', label: 'Active' },
    { value: 'PAID', label: 'Paid' }
  ];

  constructor(private companyApiService: CompanyApiService) {}

  ngOnInit(): void {
    this.loadInvoices();
  }

  /**
   * Load invoices with current filters
   */
  loadInvoices(): void {
    this.isLoading = true;
    this.errorMessage = null;

    const status = this.selectedStatus === 'ALL' ? undefined : this.selectedStatus;

    this.companyApiService.getInvoices(this.currentPage, this.pageSize, status)
      .subscribe({
        next: (response: InvoiceListResponse) => {
          this.invoices = response.invoices;
          this.totalInvoices = response.total;
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = 'Failed to load invoices. Please try again.';
          this.isLoading = false;
          console.error('Error loading invoices:', error);
        }
      });
  }

  /**
   * Handle status filter change
   */
  onStatusChange(): void {
    this.currentPage = 0; // Reset to first page
    this.loadInvoices();
  }

  /**
   * View invoice details
   */
  viewInvoice(invoice: Invoice): void {
    // TODO: Navigate to invoice detail page
    console.log('View invoice:', invoice.id);
  }

  /**
   * Mark invoice as paid
   */
  markAsPaid(invoice: Invoice): void {
    if (invoice.status !== 'PENDING') {
      return;
    }

    const confirmMessage = `Are you sure you want to mark invoice ${invoice.invoiceNumber} as paid?`;
    if (!confirm(confirmMessage)) {
      return;
    }

    const today = new Date().toISOString().split('T')[0]; // YYYY-MM-DD format

    this.companyApiService.markInvoiceAsPaid(invoice.id, invoice.amount, today)
      .subscribe({
        next: () => {
          // Reload invoices after successful payment
          this.loadInvoices();
        },
        error: (error) => {
          this.errorMessage = 'Failed to mark invoice as paid. Please try again.';
          console.error('Error marking invoice as paid:', error);
        }
      });
  }

  /**
   * Check if invoice can be marked as paid
   */
  canMarkAsPaid(invoice: Invoice): boolean {
    return invoice.status === 'PENDING';
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
   * Format currency
   */
  formatCurrency(value: number): string {
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
      this.loadInvoices();
    }
  }

  /**
   * Go to previous page
   */
  previousPage(): void {
    if (this.hasPreviousPage()) {
      this.currentPage--;
      this.loadInvoices();
    }
  }

  /**
   * Check if there is a next page
   */
  hasNextPage(): boolean {
    return (this.currentPage + 1) * this.pageSize < this.totalInvoices;
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
    return Math.ceil(this.totalInvoices / this.pageSize);
  }

  /**
   * Expose Math to template
   */
  Math = Math;
}

