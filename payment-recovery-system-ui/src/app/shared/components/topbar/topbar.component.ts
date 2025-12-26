import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';

/**
 * Topbar Component
 * Displays top navigation bar with user info and actions
 */
@Component({
  selector: 'app-topbar',
  templateUrl: './topbar.component.html',
  styleUrls: ['./topbar.component.scss']
})
export class TopbarComponent implements OnInit {
  currentUser: any = null;
  userRole: 'ADMIN' | 'COMPANY' | null = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Get current user
    this.currentUser = this.authService.getCurrentUser();
    this.userRole = this.authService.getCurrentRole();

    // Subscribe to user changes
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.userRole = user?.role || null;
    });
  }

  /**
   * Get user display name
   */
  getUserDisplayName(): string {
    return this.currentUser?.username || 'User';
  }

  /**
   * Get role display name
   */
  getRoleDisplayName(): string {
    return this.userRole || 'Unknown';
  }

  /**
   * Logout user
   */
  logout(): void {
    this.authService.logout();
  }

  /**
   * Navigate to settings
   */
  goToSettings(): void {
    if (this.userRole === 'ADMIN') {
      this.router.navigate(['/admin/settings']);
    } else if (this.userRole === 'COMPANY') {
      this.router.navigate(['/company/settings']);
    }
  }
}

