import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { TokenService } from '../services/token.service';
import { AuthService } from '../services/auth.service';

/**
 * HTTP Interceptor for authentication
 * Responsibilities:
 * - Attach JWT token to every request
 * - Handle 401 Unauthorized responses
 * - Redirect to login on session expiry
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(
    private tokenService: TokenService,
    private authService: AuthService,
    private router: Router
  ) {}

  /**
   * Intercept HTTP requests
   * Adds JWT token to Authorization header and handles 401 errors
   */
  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Get JWT token from localStorage
    const token = this.tokenService.getToken();

    // Clone request and add Authorization header if token exists
    let authRequest = request;
    if (token) {
      authRequest = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    // Handle request and catch errors
    return next.handle(authRequest).pipe(
      catchError((error: HttpErrorResponse) => {
        // Handle 401 Unauthorized - Session expired or invalid token
        if (error.status === 401) {
          this.handleUnauthorized();
        }

        // Re-throw error for component-level error handling
        return throwError(() => error);
      })
    );
  }

  /**
   * Handle 401 Unauthorized error
   * Logs out user and redirects to login page
   */
  private handleUnauthorized(): void {
    // Clear authentication data
    this.tokenService.clear();
    this.authService.logout();

    // Get current URL for redirect after login
    const currentUrl = this.router.url;

    // Redirect to login with session expired flag and return URL
    this.router.navigate(['/auth/login'], {
      queryParams: {
        sessionExpired: true,
        returnUrl: currentUrl !== '/auth/login' ? currentUrl : undefined
      }
    });
  }
}

