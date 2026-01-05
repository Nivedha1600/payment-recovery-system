import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { LoginRequest, LoginResponse, AuthUser, CompanyRegistrationRequest, CompanyRegistrationResponse } from '../models/auth.model';
import { TokenService } from './token.service';
import { environment } from '../../../environments/environment';

/**
 * Authentication service
 * Handles login, logout, and authentication state management
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;
  
  private currentUserSubject = new BehaviorSubject<AuthUser | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private tokenService: TokenService,
    private router: Router
  ) {
    // Initialize user from localStorage if token exists
    this.initializeUser();
  }

  /**
   * Initialize user from localStorage on service creation
   */
  private initializeUser(): void {
    const token = this.tokenService.getToken();
    const role = this.tokenService.getRole();
    const username = this.tokenService.getUsername();

    if (token && role && username) {
      this.currentUserSubject.next({
        username,
        role,
        token
      });
    }
  }

  /**
   * Login user
   * @param credentials Login credentials
   * @returns Observable of login response
   */
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response: LoginResponse) => {
        // Store token and role in localStorage
        this.tokenService.setToken(response.token);
        this.tokenService.setRole(response.role);
        this.tokenService.setUsername(credentials.username);

        // Update current user
        const user: AuthUser = {
          username: credentials.username,
          role: response.role,
          token: response.token
        };
        this.currentUserSubject.next(user);
      }),
      catchError((error) => {
        console.error('Login error:', error);
        return throwError(() => error);
      })
    );
  }

  /**
   * Logout user
   */
  logout(): void {
    // Clear localStorage
    this.tokenService.clear();
    
    // Clear current user
    this.currentUserSubject.next(null);
    
    // Redirect to login
    this.router.navigate(['/auth/login']);
  }

  /**
   * Get current user
   */
  getCurrentUser(): AuthUser | null {
    return this.currentUserSubject.value;
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    return this.tokenService.hasToken() && this.currentUserSubject.value !== null;
  }

  /**
   * Get current user role
   */
  getCurrentRole(): 'ADMIN' | 'COMPANY' | null {
    const user = this.getCurrentUser();
    return user?.role || null;
  }

  /**
   * Check if user has specific role
   */
  hasRole(role: 'ADMIN' | 'COMPANY'): boolean {
    const currentRole = this.getCurrentRole();
    return currentRole === role;
  }

  /**
   * Check if user is ADMIN
   */
  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  /**
   * Check if user is COMPANY
   */
  isCompany(): boolean {
    return this.hasRole('COMPANY');
  }

  /**
   * Get authentication token
   */
  getToken(): string | null {
    return this.tokenService.getToken();
  }

  /**
   * Get company ID from JWT token
   */
  getCompanyId(): number | null {
    return this.tokenService.getCompanyId();
  }

  /**
   * Register a new company
   * @param registrationData Company registration data
   * @returns Observable of registration response
   */
  registerCompany(registrationData: CompanyRegistrationRequest): Observable<CompanyRegistrationResponse> {
    return this.http.post<CompanyRegistrationResponse>(`${this.apiUrl}/register`, registrationData).pipe(
      catchError((error) => {
        console.error('Registration error:', error);
        return throwError(() => error);
      })
    );
  }
}

