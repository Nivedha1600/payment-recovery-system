import { Injectable } from '@angular/core';

/**
 * Service for managing JWT token storage in localStorage
 */
@Injectable({ providedIn: 'root' })
export class TokenService {
  private readonly TOKEN_KEY = 'auth_token';
  private readonly ROLE_KEY = 'user_role';
  private readonly USERNAME_KEY = 'username';

  /**
   * Store authentication token in localStorage
   */
  setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  /**
   * Get authentication token from localStorage
   */
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Remove authentication token from localStorage
   */
  removeToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  /**
   * Check if token exists
   */
  hasToken(): boolean {
    return this.getToken() !== null;
  }

  /**
   * Store user role in localStorage
   */
  setRole(role: 'ADMIN' | 'COMPANY'): void {
    localStorage.setItem(this.ROLE_KEY, role);
  }

  /**
   * Get user role from localStorage
   */
  getRole(): 'ADMIN' | 'COMPANY' | null {
    const role = localStorage.getItem(this.ROLE_KEY);
    return role as 'ADMIN' | 'COMPANY' | null;
  }

  /**
   * Remove user role from localStorage
   */
  removeRole(): void {
    localStorage.removeItem(this.ROLE_KEY);
  }

  /**
   * Store username in localStorage
   */
  setUsername(username: string): void {
    localStorage.setItem(this.USERNAME_KEY, username);
  }

  /**
   * Get username from localStorage
   */
  getUsername(): string | null {
    return localStorage.getItem(this.USERNAME_KEY);
  }

  /**
   * Remove username from localStorage
   */
  removeUsername(): void {
    localStorage.removeItem(this.USERNAME_KEY);
  }

  /**
   * Get company ID from JWT token
   * Extracts companyId from the JWT token payload
   */
  getCompanyId(): number | null {
    const token = this.getToken();
    if (!token) {
      return null;
    }

    try {
      // JWT token structure: header.payload.signature
      const payload = token.split('.')[1];
      if (!payload) {
        return null;
      }

      // Decode base64 payload
      const decodedPayload = JSON.parse(atob(payload));
      return decodedPayload.companyId || null;
    } catch (error) {
      console.error('Error decoding JWT token:', error);
      return null;
    }
  }

  /**
   * Clear all authentication data from localStorage
   */
  clear(): void {
    this.removeToken();
    this.removeRole();
    this.removeUsername();
  }
}

