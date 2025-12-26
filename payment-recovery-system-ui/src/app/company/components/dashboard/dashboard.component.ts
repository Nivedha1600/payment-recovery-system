import { Component, OnInit } from '@angular/core';
import { CompanyApiService } from '../../services/company-api.service';
import { CompanyDashboardMetrics } from '../../models/dashboard.model';

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

  constructor(private companyApiService: CompanyApiService) {}

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
   * Refresh metrics
   */
  refresh(): void {
    this.loadMetrics();
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
}

