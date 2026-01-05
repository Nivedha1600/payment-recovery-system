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

export interface CompanyRegistrationRequest {
  companyName: string;
  gstNumber?: string;
  contactEmail: string;
  contactPhone?: string;
  username: string;
  password: string;
}

export interface CompanyRegistrationResponse {
  companyId: number;
  companyName: string;
  message: string;
  requiresApproval: boolean;
}

