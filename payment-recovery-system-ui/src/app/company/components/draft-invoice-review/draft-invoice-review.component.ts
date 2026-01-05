import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CompanyApiService } from '../../services/company-api.service';
import { DraftInvoice, ExtractedInvoiceData } from '../../models/extracted-invoice.model';

/**
 * Draft Invoice Review Component
 * Allows users to review and edit extracted invoice data before activation
 */
@Component({
  selector: 'app-draft-invoice-review',
  templateUrl: './draft-invoice-review.component.html',
  styleUrls: ['./draft-invoice-review.component.scss']
})
export class DraftInvoiceReviewComponent implements OnInit {
  invoiceId: number | null = null;
  draftInvoice: DraftInvoice | null = null;
  extractedData: ExtractedInvoiceData | null = null;
  
  // Editable form fields
  formData = {
    invoiceNumber: '',
    invoiceDate: '',
    dueDate: '',
    amount: null as number | null
  };

  isLoading = false;
  isSaving = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private companyApiService: CompanyApiService
  ) {}

  ngOnInit(): void {
    // Get invoice ID from route
    this.route.params.subscribe(params => {
      this.invoiceId = +params['id'];
      if (this.invoiceId) {
        this.loadDraftInvoice();
      }
    });
  }

  /**
   * Load draft invoice data
   */
  loadDraftInvoice(): void {
    if (!this.invoiceId) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    // TODO: Replace with actual API call when backend is ready
    // For now, use mock data
    this.companyApiService.getInvoiceById(this.invoiceId)
      .subscribe({
        next: (invoice) => {
          // Mock extracted data for now
          this.extractedData = this.getMockExtractedData();
          
          // Initialize form with extracted data or existing invoice data
          this.formData = {
            invoiceNumber: invoice.invoiceNumber || this.extractedData.invoiceNumber || '',
            invoiceDate: invoice.invoiceDate || this.extractedData.invoiceDate || '',
            dueDate: invoice.dueDate || this.extractedData.dueDate || '',
            amount: invoice.amount || this.extractedData.amount || null
          };

          this.draftInvoice = {
            id: invoice.id,
            invoiceNumber: invoice.invoiceNumber ?? undefined,
            invoiceDate: invoice.invoiceDate ?? undefined,
            dueDate: invoice.dueDate ?? undefined,
            amount: invoice.amount ?? undefined,
            customerId: invoice.customerId ?? undefined,
            customerName: invoice.customerName ?? undefined,
            status: 'DRAFT',
            extractedData: this.extractedData
          };

          this.isLoading = false;
        },
        error: (error) => {
          // If API fails, use mock data for development
          console.warn('API call failed, using mock data:', error);
          this.loadMockData();
          this.isLoading = false;
        }
      });
  }

  /**
   * Load mock data for development
   */
  loadMockData(): void {
    this.extractedData = this.getMockExtractedData();
    this.draftInvoice = {
      id: this.invoiceId || 1,
      invoiceNumber: this.extractedData.invoiceNumber,
      invoiceDate: this.extractedData.invoiceDate,
      dueDate: this.extractedData.dueDate,
      amount: this.extractedData.amount,
      customerName: this.extractedData.customerName,
      status: 'DRAFT',
      extractedData: this.extractedData
    };

    this.formData = {
      invoiceNumber: this.extractedData.invoiceNumber || '',
      invoiceDate: this.extractedData.invoiceDate || '',
      dueDate: this.extractedData.dueDate || '',
      amount: this.extractedData.amount || null
    };
  }

  /**
   * Get mock extracted data
   */
  getMockExtractedData(): ExtractedInvoiceData {
    const today = new Date();
    const dueDate = new Date(today);
    dueDate.setDate(dueDate.getDate() + 30);

    return {
      invoiceNumber: 'INV-2024-001',
      invoiceDate: today.toISOString().split('T')[0],
      dueDate: dueDate.toISOString().split('T')[0],
      amount: 5000.00,
      customerName: 'Acme Corporation',
      customerEmail: 'billing@acme.com',
      customerPhone: '+1-555-0123',
      lineItems: [
        {
          description: 'Product A',
          quantity: 10,
          unitPrice: 500.00,
          total: 5000.00
        }
      ],
      taxAmount: 0,
      totalAmount: 5000.00,
      currency: 'USD',
      notes: 'Payment terms: Net 30'
    };
  }

  /**
   * Confirm and activate invoice
   */
  confirmInvoice(): void {
    // Validate form
    if (!this.isFormValid()) {
      this.errorMessage = 'Please fill in all required fields (Invoice Number, Invoice Date, Amount).';
      return;
    }

    const confirmMessage = 'Are you sure you want to activate this invoice? It will become eligible for reminders.';
    if (!confirm(confirmMessage)) {
      return;
    }

    this.isSaving = true;
    this.errorMessage = null;
    this.successMessage = null;

    // TODO: Call API to activate invoice
    // This should update the invoice with the form data and change status from DRAFT to PENDING
    if (this.invoiceId) {
      this.activateInvoice();
    }
  }

  /**
   * Activate invoice via API
   */
  activateInvoice(): void {
    if (!this.invoiceId) {
      this.errorMessage = 'Invalid invoice ID.';
      this.isSaving = false;
      return;
    }

    this.companyApiService.confirmInvoice(
      this.invoiceId,
      this.formData.invoiceNumber,
      this.formData.invoiceDate,
      this.formData.dueDate || '',
      this.formData.amount!,
      this.draftInvoice?.customerId
    ).subscribe({
      next: () => {
        this.isSaving = false;
        this.successMessage = 'Invoice activated successfully! It is now eligible for reminders.';
        
        // Redirect to invoices list after 2 seconds
        setTimeout(() => {
          this.router.navigate(['/company/invoices']);
        }, 2000);
      },
      error: (error) => {
        this.isSaving = false;
        this.errorMessage = error.error?.message || 'Failed to activate invoice. Please try again.';
        console.error('Error activating invoice:', error);
      }
    });
  }

  /**
   * Validate form
   */
  isFormValid(): boolean {
    return !!(
      this.formData.invoiceNumber &&
      this.formData.invoiceDate &&
      this.formData.amount &&
      this.formData.amount > 0
    );
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
  formatDate(dateString: string | null | undefined): string {
    if (!dateString) {
      return 'N/A';
    }
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  /**
   * Check if line items exist and have items
   */
  hasLineItems(): boolean {
    return !!(this.extractedData?.lineItems && this.extractedData.lineItems.length > 0);
  }

  /**
   * Cancel and go back
   */
  cancel(): void {
    this.router.navigate(['/company/draft-invoices']);
  }
}

