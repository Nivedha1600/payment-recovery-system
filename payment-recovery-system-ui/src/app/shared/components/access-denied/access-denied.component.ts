import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

/**
 * Access Denied component
 * Displayed when user tries to access a route they don't have permission for
 */
@Component({
  selector: 'app-access-denied',
  templateUrl: './access-denied.component.html',
  styleUrls: ['./access-denied.component.scss']
})
export class AccessDeniedComponent implements OnInit {
  attemptedUrl: string | null = null;
  userRole: string | null = null;
  currentUserRole: 'ADMIN' | 'COMPANY' | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Get query parameters
    this.attemptedUrl = this.route.snapshot.queryParams['attemptedUrl'] || null;
    this.userRole = this.route.snapshot.queryParams['userRole'] || null;
    
    // Get current user role
    this.currentUserRole = this.authService.getCurrentRole();
  }

  /**
   * Navigate to appropriate dashboard based on user role
   */
  goToDashboard(): void {
    if (this.currentUserRole === 'ADMIN') {
      this.router.navigate(['/admin/dashboard']);
    } else if (this.currentUserRole === 'COMPANY') {
      this.router.navigate(['/company/dashboard']);
    } else {
      this.router.navigate(['/auth/login']);
    }
  }

  /**
   * Go back to previous page
   */
  goBack(): void {
    if (this.attemptedUrl) {
      this.router.navigateByUrl(this.attemptedUrl);
    } else {
      this.goToDashboard();
    }
  }
}

