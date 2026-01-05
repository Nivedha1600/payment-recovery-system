import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Company, CompanyListResponse, CompanyStatusUpdate } from '../models/company.model';
import { PlatformMetrics } from '../models/dashboard.model';

/**
 * Admin API service
 * Handles all admin-related API calls
 */
@Injectable({ providedIn: 'root' })
export class AdminApiService {
  private readonly apiUrl = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  /**
   * Get platform metrics for dashboard
   */
  getPlatformMetrics(): Observable<PlatformMetrics> {
    return this.http.get<PlatformMetrics>(`${this.apiUrl}/metrics`);
  }

  /**
   * Get list of all companies
   */
  getCompanies(page: number = 0, pageSize: number = 10, search?: string): Observable<CompanyListResponse> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', pageSize.toString());

    if (search) {
      params = params.set('search', search);
    }

    return this.http.get<CompanyListResponse>(`${this.apiUrl}/companies`, { params });
  }

  /**
   * Get company by ID
   */
  getCompanyById(companyId: number): Observable<Company> {
    return this.http.get<Company>(`${this.apiUrl}/companies/${companyId}`);
  }

  /**
   * Update company status (activate/deactivate)
   */
  updateCompanyStatus(companyId: number, isActive: boolean): Observable<Company> {
    const update: CompanyStatusUpdate = { companyId, isActive };
    return this.http.patch<Company>(`${this.apiUrl}/companies/${companyId}/status`, update);
  }

  /**
   * Activate company
   */
  activateCompany(companyId: number): Observable<Company> {
    return this.updateCompanyStatus(companyId, true);
  }

  /**
   * Deactivate company
   */
  deactivateCompany(companyId: number): Observable<Company> {
    return this.updateCompanyStatus(companyId, false);
  }

  /**
   * Approve company registration
   */
  approveCompany(companyId: number): Observable<Company> {
    return this.http.post<Company>(`${this.apiUrl}/companies/${companyId}/approve`, {});
  }

  /**
   * Reject company registration
   */
  rejectCompany(companyId: number): Observable<Company> {
    return this.http.post<Company>(`${this.apiUrl}/companies/${companyId}/reject`, {});
  }
}

