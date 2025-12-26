import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Role-based access guard
 * Protects routes that require specific roles
 * Must be used after AuthGuard to ensure user is authenticated
 */
@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    // First check if user is authenticated
    // This should already be checked by AuthGuard, but double-check for safety
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/auth/login'], {
        queryParams: { returnUrl: state.url }
      });
      return false;
    }

    // Get required roles from route data
    const requiredRoles = route.data['roles'] as Array<'ADMIN' | 'COMPANY'>;
    
    // If no roles specified, allow access (route is protected by AuthGuard only)
    if (!requiredRoles || requiredRoles.length === 0) {
      return true;
    }

    // Get current user role
    const userRole = this.authService.getCurrentRole();

    // Check if user has one of the required roles
    if (userRole && requiredRoles.includes(userRole)) {
      return true;
    }

    // User does not have required role - redirect to access denied or appropriate dashboard
    this.handleRoleMismatch(userRole, state.url);
    return false;
  }

  /**
   * Handle role mismatch - redirect to access denied or appropriate dashboard
   */
  private handleRoleMismatch(userRole: 'ADMIN' | 'COMPANY' | null, attemptedUrl: string): void {
    // Try to navigate to access denied page first
    // If access-denied component doesn't exist, fall back to dashboard redirect
    try {
      this.router.navigate(['/access-denied'], {
        queryParams: { 
          attemptedUrl: attemptedUrl,
          userRole: userRole || 'UNKNOWN'
        }
      });
    } catch {
      // If access-denied route doesn't exist, redirect to appropriate dashboard
      if (userRole === 'ADMIN') {
        this.router.navigate(['/admin/dashboard'], {
          queryParams: { accessDenied: true }
        });
      } else if (userRole === 'COMPANY') {
        this.router.navigate(['/company/dashboard'], {
          queryParams: { accessDenied: true }
        });
      } else {
        // Unknown role or no role - redirect to login
        this.router.navigate(['/auth/login'], {
          queryParams: { 
            returnUrl: attemptedUrl,
            accessDenied: true
          }
        });
      }
    }
  }
}

