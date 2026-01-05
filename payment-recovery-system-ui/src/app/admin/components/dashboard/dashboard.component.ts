import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AdminApiService } from '../../services/admin-api.service';
import { PlatformMetrics } from '../../models/dashboard.model';
import { Company } from '../../models/company.model';
import { AuthService } from '../../../core/services/auth.service';

/**
 * Admin Dashboard Component
 * Displays platform-level metrics and overview
 */
@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class AdminDashboardComponent implements OnInit {
  metrics: PlatformMetrics | null = null;
  companiesList: Company[] = [];
  isLoading = false;
  errorMessage: string | null = null;

  constructor(
    private adminApiService: AdminApiService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadMetrics();
    this.loadCompanies();
  }

  /**
   * Load platform metrics
   */
  loadMetrics(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.adminApiService.getPlatformMetrics().subscribe({
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
   * Load companies list for table
   */
  loadCompanies(): void {
    this.adminApiService.getCompanies(0, 10).subscribe({
      next: (response) => {
        this.companiesList = response.companies;
      },
      error: (error) => {
        console.error('Error loading companies:', error);
        // Don't show error for companies table, just log it
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
   * Logout user
   */
  logout(): void {
    this.authService.logout();
  }

  /**
   * Navigate to company management page
   */
  navigateToCompanies(): void {
    this.router.navigate(['/admin/companies']);
  }
}

