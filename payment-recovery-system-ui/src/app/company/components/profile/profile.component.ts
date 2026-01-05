import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { CompanyApiService } from '../../services/company-api.service';

/**
 * Company Profile Component
 * Displays and allows editing of company profile information
 */
@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  companyInfo: any = null;
  isLoading = false;
  isSaving = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  // Editable form fields
  formData = {
    name: '',
    gstNumber: '',
    contactEmail: '',
    contactPhone: ''
  };

  constructor(
    private router: Router,
    private authService: AuthService,
    private companyApiService: CompanyApiService
  ) {}

  ngOnInit(): void {
    this.loadCompanyInfo();
  }

  /**
   * Load company information
   */
  loadCompanyInfo(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.companyApiService.getCompanyProfile().subscribe({
      next: (company) => {
        this.companyInfo = company;
        this.formData = {
          name: company.name || '',
          gstNumber: company.gstNumber || '',
          contactEmail: company.contactEmail || '',
          contactPhone: company.contactPhone || ''
        };
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load company profile. Please try again.';
        this.isLoading = false;
        console.error('Error loading company profile:', error);
      }
    });
  }

  /**
   * Save company profile
   */
  saveProfile(): void {
    if (!this.isFormValid()) {
      this.errorMessage = 'Please fill in all required fields (Company Name and Contact Email).';
      return;
    }

    this.isSaving = true;
    this.errorMessage = null;
    this.successMessage = null;

    this.companyApiService.updateCompanyProfile(this.formData).subscribe({
      next: (updatedCompany) => {
        this.companyInfo = updatedCompany;
        this.isSaving = false;
        this.successMessage = 'Profile updated successfully!';
        
        setTimeout(() => {
          this.successMessage = null;
        }, 3000);
      },
      error: (error) => {
        this.isSaving = false;
        this.errorMessage = error.error?.message || 'Failed to update profile. Please try again.';
        console.error('Error updating company profile:', error);
      }
    });
  }

  /**
   * Go back to dashboard
   */
  goBack(): void {
    this.router.navigate(['/company/dashboard']);
  }

  /**
   * Check if form is valid
   */
  isFormValid(): boolean {
    return !!(this.formData.name && this.formData.contactEmail);
  }
}

