/**
 * Authentication models and interfaces
 */

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  role: 'ADMIN' | 'COMPANY';
}

export interface AuthUser {
  username: string;
  role: 'ADMIN' | 'COMPANY';
  token: string;
}

