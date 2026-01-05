import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CompanyApiService } from '../../services/company-api.service';
import { CompanyDashboardMetrics } from '../../models/dashboard.model';
import { AuthService } from '../../../core/services/auth.service';

/**
 * Company Dashboard Component
 * Displays company-specific metrics and overview
 */
@Component({
  selector: 'app-company-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class CompanyDashboardComponent implements OnInit {
  metrics: CompanyDashboardMetrics | null = null;
  isLoading = false;
  errorMessage: string | null = null;

  constructor(
    private companyApiService: CompanyApiService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadMetrics();
  }

  /**
   * Load dashboard metrics
   */
  loadMetrics(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.companyApiService.getDashboardMetrics().subscribe({
      next: (metrics) => {
        this.metrics = metrics;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load dashboard metrics. Please try again.';
        this.isLoading = false;
        console.error('Error loading metrics:', error);
      }
    });
  }

  /**
   * Format number with commas
   */
  formatNumber(value: number): string {
    return value.toLocaleString();
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
   * Navigate to invoice upload page
   */
  navigateToUpload(): void {
    this.router.navigate(['/company/invoices/upload']);
  }

  /**
   * Navigate to invoices list page
   * Optionally filter by status
   */
  navigateToInvoices(status?: string): void {
    if (status) {
      this.router.navigate(['/company/invoices'], { queryParams: { status } });
    } else {
      this.router.navigate(['/company/invoices']);
    }
  }

  /**
   * Logout user
   */
  logout(): void {
    this.authService.logout();
  }
}

