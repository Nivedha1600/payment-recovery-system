import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Authentication guard
 * Protects routes that require authentication
 * Checks if user is authenticated before allowing route access
 */
@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    // Check if user is authenticated
    if (this.authService.isAuthenticated()) {
      return true;
    }

    // User is not authenticated - redirect to login
    // Store attempted URL for redirect after login
    this.router.navigate(['/auth/login'], {
      queryParams: { returnUrl: state.url }
    });
    
    return false;
  }
}

