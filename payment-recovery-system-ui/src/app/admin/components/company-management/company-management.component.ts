import { Component, OnInit } from '@angular/core';
import { AdminApiService } from '../../services/admin-api.service';
import { Company, CompanyListResponse } from '../../models/company.model';

/**
 * Company Management Component
 * Allows admin to view, activate, and deactivate companies
 */
@Component({
  selector: 'app-company-management',
  templateUrl: './company-management.component.html',
  styleUrls: ['./company-management.component.scss']
})
export class CompanyManagementComponent implements OnInit {
  companies: Company[] = [];
  totalCompanies = 0;
  currentPage = 0;
  pageSize = 10;
  searchTerm = '';
  isLoading = false;
  errorMessage: string | null = null;
  
  // Action states
  updatingCompanyId: number | null = null;

  constructor(private adminApiService: AdminApiService) {}

  ngOnInit(): void {
    this.loadCompanies();
  }

  /**
   * Load companies with pagination and search
   */
  loadCompanies(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.adminApiService.getCompanies(this.currentPage, this.pageSize, this.searchTerm || undefined)
      .subscribe({
        next: (response: CompanyListResponse) => {
          this.companies = response.companies;
          this.totalCompanies = response.total;
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = 'Failed to load companies. Please try again.';
          this.isLoading = false;
          console.error('Error loading companies:', error);
        }
      });
  }

  /**
   * Handle search
   */
  onSearch(): void {
    this.currentPage = 0; // Reset to first page
    this.loadCompanies();
  }

  /**
   * Clear search
   */
  clearSearch(): void {
    this.searchTerm = '';
    this.currentPage = 0;
    this.loadCompanies();
  }

  /**
   * Toggle company status (activate/deactivate)
   */
  toggleCompanyStatus(company: Company): void {
    if (this.updatingCompanyId !== null) {
      return; // Already updating
    }

    const confirmMessage = company.isActive
      ? `Are you sure you want to deactivate ${company.name}?`
      : `Are you sure you want to activate ${company.name}?`;

    if (!confirm(confirmMessage)) {
      return;
    }

    this.updatingCompanyId = company.id;
    const newStatus = !company.isActive;

    this.adminApiService.updateCompanyStatus(company.id, newStatus)
      .subscribe({
        next: (updatedCompany) => {
          // Update company in list
          const index = this.companies.findIndex(c => c.id === updatedCompany.id);
          if (index !== -1) {
            this.companies[index] = updatedCompany;
          }
          this.updatingCompanyId = null;
        },
        error: (error) => {
          this.errorMessage = `Failed to ${newStatus ? 'activate' : 'deactivate'} company. Please try again.`;
          this.updatingCompanyId = null;
          console.error('Error updating company status:', error);
        }
      });
  }

  /**
   * Activate company
   */
  activateCompany(company: Company): void {
    if (company.isActive) {
      return;
    }
    this.toggleCompanyStatus(company);
  }

  /**
   * Deactivate company
   */
  deactivateCompany(company: Company): void {
    if (!company.isActive) {
      return;
    }
    this.toggleCompanyStatus(company);
  }

  /**
   * Approve company registration
   */
  approveCompany(company: Company): void {
    if (this.updatingCompanyId !== null) {
      return;
    }

    if (!confirm(`Are you sure you want to approve ${company.name}? This will allow their users to login.`)) {
      return;
    }

    this.updatingCompanyId = company.id;

    this.adminApiService.approveCompany(company.id)
      .subscribe({
        next: (updatedCompany) => {
          const index = this.companies.findIndex(c => c.id === updatedCompany.id);
          if (index !== -1) {
            this.companies[index] = updatedCompany;
          }
          this.updatingCompanyId = null;
        },
        error: (error) => {
          this.errorMessage = 'Failed to approve company. Please try again.';
          this.updatingCompanyId = null;
          console.error('Error approving company:', error);
        }
      });
  }

  /**
   * Go to next page
   */
  nextPage(): void {
    if (this.hasNextPage()) {
      this.currentPage++;
      this.loadCompanies();
    }
  }

  /**
   * Go to previous page
   */
  previousPage(): void {
    if (this.hasPreviousPage()) {
      this.currentPage--;
      this.loadCompanies();
    }
  }

  /**
   * Check if there is a next page
   */
  hasNextPage(): boolean {
    return (this.currentPage + 1) * this.pageSize < this.totalCompanies;
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
    return Math.ceil(this.totalCompanies / this.pageSize);
  }

  /**
   * Expose Math to template
   */
  Math = Math;

  /**
   * Format date
   */
  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }
}

