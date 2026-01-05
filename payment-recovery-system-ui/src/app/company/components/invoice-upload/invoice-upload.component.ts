import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { CompanyApiService } from '../../services/company-api.service';
import { AuthService } from '../../../core/services/auth.service';

/**
 * Invoice Upload Component
 * Allows users to upload invoice files or manually enter invoice data
 */
@Component({
  selector: 'app-invoice-upload',
  templateUrl: './invoice-upload.component.html',
  styleUrls: ['./invoice-upload.component.scss']
})
export class InvoiceUploadComponent implements OnInit {
  // File upload
  selectedFile: File | null = null;
  fileName: string = '';
  fileType: string = '';
  isUploading = false;
  
  // Manual entry
  useManualEntry = false;
  manualForm = {
    invoiceNumber: '',
    customerId: null as number | null,
    invoiceDate: '',
    dueDate: '',
    amount: null as number | null
  };

  // Success/Error states
  uploadSuccess = false;
  successMessage: string = '';
  errorMessage: string | null = null;

  // Accepted file types
  acceptedFileTypes = '.pdf,.png,.jpg,.jpeg,.doc,.docx,.xls,.xlsx,.csv';
  maxFileSize = 10 * 1024 * 1024; // 10MB

  constructor(
    private companyApiService: CompanyApiService,
    private router: Router,
    private authService: AuthService,
    private location: Location
  ) {}

  ngOnInit(): void {
  }

  /**
   * Handle file selection
   */
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      
      // Validate file type
      if (!this.isValidFileType(file)) {
        this.errorMessage = 'Invalid file type. Please upload PDF, Image, DOC, Excel, or CSV files.';
        return;
      }

      // Validate file size
      if (file.size > this.maxFileSize) {
        this.errorMessage = `File size exceeds ${this.maxFileSize / (1024 * 1024)}MB limit.`;
        return;
      }

      this.selectedFile = file;
      this.fileName = file.name;
      this.fileType = file.type || this.getFileTypeFromExtension(file.name);
      this.errorMessage = null;
      this.useManualEntry = false;
    }
  }

  /**
   * Validate file type
   */
  isValidFileType(file: File): boolean {
    const validTypes = [
      'application/pdf',
      'image/png',
      'image/jpeg',
      'image/jpg',
      'application/msword',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
      'application/vnd.ms-excel',
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      'text/csv',
      'application/csv',
      'text/plain' // CSV files sometimes have text/plain MIME type
    ];

    const validExtensions = ['.pdf', '.png', '.jpg', '.jpeg', '.doc', '.docx', '.xls', '.xlsx', '.csv'];
    const fileExtension = '.' + file.name.split('.').pop()?.toLowerCase();

    return validTypes.includes(file.type) || validExtensions.includes(fileExtension);
  }

  /**
   * Get file type from extension
   */
  getFileTypeFromExtension(fileName: string): string {
    const extension = fileName.split('.').pop()?.toLowerCase();
    const typeMap: { [key: string]: string } = {
      'pdf': 'application/pdf',
      'png': 'image/png',
      'jpg': 'image/jpeg',
      'jpeg': 'image/jpeg',
      'doc': 'application/msword',
      'docx': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
      'xls': 'application/vnd.ms-excel',
      'xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      'csv': 'text/csv'
    };
    return typeMap[extension || ''] || 'application/octet-stream';
  }

  /**
   * Remove selected file
   */
  removeFile(): void {
    this.selectedFile = null;
    this.fileName = '';
    this.fileType = '';
  }

  /**
   * Toggle between file upload and manual entry
   */
  toggleEntryMode(): void {
    this.useManualEntry = !this.useManualEntry;
    if (this.useManualEntry) {
      this.selectedFile = null;
      this.fileName = '';
    } else {
      this.resetManualForm();
    }
    this.errorMessage = null;
    this.uploadSuccess = false;
  }

  /**
   * Reset manual form
   */
  resetManualForm(): void {
    this.manualForm = {
      invoiceNumber: '',
      customerId: null,
      invoiceDate: '',
      dueDate: '',
      amount: null
    };
  }

  /**
   * Submit invoice (file upload or manual entry)
   */
  onSubmit(): void {
    this.errorMessage = null;
    this.uploadSuccess = false;

    if (this.useManualEntry) {
      this.submitManualEntry();
    } else {
      this.submitFileUpload();
    }
  }

  /**
   * Submit file upload
   */
  submitFileUpload(): void {
    if (!this.selectedFile) {
      this.errorMessage = 'Please select a file to upload.';
      return;
    }

    this.isUploading = true;
    this.errorMessage = null;

    // Create FormData for file upload
    const formData = new FormData();
    formData.append('file', this.selectedFile);
    
    // Get companyId from JWT token
    const companyId = this.authService.getCompanyId();
    if (!companyId) {
      this.errorMessage = 'Unable to determine company. Please login again.';
      this.isUploading = false;
      return;
    }

    // Call API to upload invoice file
    // Note: This endpoint should match the backend API
    this.companyApiService.uploadInvoiceFile(formData, companyId)
      .subscribe({
        next: (response) => {
          this.isUploading = false;
          this.uploadSuccess = true;
          this.successMessage = `Invoice uploaded successfully! Invoice ID: ${response.invoiceId}. Status: DRAFT`;
          
          // Clear file selection
          this.selectedFile = null;
          this.fileName = '';
          
          // Reset form after 5 seconds
          setTimeout(() => {
            this.uploadSuccess = false;
          }, 5000);
        },
        error: (error) => {
          this.isUploading = false;
          this.errorMessage = error.error?.message || 'Failed to upload invoice. Please try again.';
          console.error('Error uploading invoice:', error);
        }
      });
  }

  /**
   * Submit manual entry
   */
  submitManualEntry(): void {
    // Validate manual form
    if (!this.manualForm.invoiceNumber || !this.manualForm.amount || !this.manualForm.invoiceDate) {
      this.errorMessage = 'Please fill in all required fields (Invoice Number, Amount, Invoice Date).';
      return;
    }

    this.isUploading = true;
    this.errorMessage = null;

    // Get companyId from JWT token
    const companyId = this.authService.getCompanyId();
    if (!companyId) {
      this.errorMessage = 'Unable to determine company. Please login again.';
      this.isUploading = false;
      return;
    }

    // Call API to create DRAFT invoice manually
    this.companyApiService.createInvoice(
      this.manualForm.invoiceNumber,
      this.manualForm.invoiceDate,
      this.manualForm.dueDate || null,
      this.manualForm.amount!,
      this.manualForm.customerId || undefined
    ).subscribe({
      next: (response) => {
        this.isUploading = false;
        this.uploadSuccess = true;
        this.successMessage = `Invoice created successfully as DRAFT! Invoice ID: ${response.invoiceId}`;
        this.resetManualForm();
        
        setTimeout(() => {
          this.uploadSuccess = false;
        }, 5000);
      },
      error: (error) => {
        this.isUploading = false;
        this.errorMessage = error.error?.message || 'Failed to create invoice. Please try again.';
        console.error('Error creating invoice:', error);
      }
    });
  }

  /**
   * Check if form is valid
   */
  isFormValid(): boolean {
    if (this.useManualEntry) {
      return !!(
        this.manualForm.invoiceNumber &&
        this.manualForm.amount &&
        this.manualForm.invoiceDate
      );
    } else {
      return !!this.selectedFile;
    }
  }

  /**
   * Format file size
   */
  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  }

  /**
   * Navigate to invoices list
   */
  navigateToInvoices(): void {
    this.router.navigate(['/company/invoices']);
  }

  /**
   * Go back to previous page
   */
  goBack(): void {
    this.location.back();
  }

}

