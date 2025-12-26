import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { filter } from 'rxjs/operators';

/**
 * Sidebar Component
 * Displays navigation menu based on user role
 * Shows Admin menus only for ADMIN, Company menus only for COMPANY
 */
@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  currentUserRole: 'ADMIN' | 'COMPANY' | null = null;
  currentRoute: string = '';

  // Admin menu items
  adminMenuItems = [
    {
      label: 'Dashboard',
      icon: 'dashboard',
      route: '/admin/dashboard'
    },
    {
      label: 'Companies',
      icon: 'business',
      route: '/admin/companies'
    }
  ];

  // Company menu items
  companyMenuItems = [
    {
      label: 'Dashboard',
      icon: 'dashboard',
      route: '/company/dashboard'
    },
    {
      label: 'Invoices',
      icon: 'receipt',
      route: '/company/invoices'
    },
    {
      label: 'Draft Invoices',
      icon: 'draft',
      route: '/company/draft-invoices'
    },
    {
      label: 'Customers',
      icon: 'people',
      route: '/company/customers'
    },
    {
      label: 'Payments',
      icon: 'payment',
      route: '/company/payments'
    }
  ];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Get current user role
    this.currentUserRole = this.authService.getCurrentRole();
    this.currentRoute = this.router.url;

    // Subscribe to route changes to update active menu item
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        this.currentRoute = event.url;
      });

    // Subscribe to user changes
    this.authService.currentUser$.subscribe(user => {
      this.currentUserRole = user?.role || null;
    });
  }

  /**
   * Check if menu item is active
   */
  isActive(route: string): boolean {
    return this.currentRoute.startsWith(route);
  }

  /**
   * Navigate to route
   */
  navigateTo(route: string): void {
    this.router.navigate([route]);
  }

  /**
   * Logout user
   */
  logout(): void {
    this.authService.logout();
  }

  /**
   * Check if user is admin
   */
  isAdmin(): boolean {
    return this.currentUserRole === 'ADMIN';
  }

  /**
   * Check if user is company
   */
  isCompany(): boolean {
    return this.currentUserRole === 'COMPANY';
  }

  /**
   * Get current menu items based on role
   */
  getMenuItems(): any[] {
    if (this.isAdmin()) {
      return this.adminMenuItems;
    } else if (this.isCompany()) {
      return this.companyMenuItems;
    }
    return [];
  }
}

