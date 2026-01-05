import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CompanyDashboardMetrics } from '../models/dashboard.model';
import { Invoice, InvoiceListResponse } from '../models/invoice.model';
import { Customer, CustomerListResponse } from '../models/customer.model';
import { Payment, PaymentListResponse } from '../models/payment.model';

/**
 * Company API service
 * Handles all company-related API calls
 */
@Injectable({ providedIn: 'root' })
export class CompanyApiService {
  private readonly apiUrl = `${environment.apiUrl}/company`;

  constructor(private http: HttpClient) {}

  /**
   * Get company dashboard metrics
   */
  getDashboardMetrics(): Observable<CompanyDashboardMetrics> {
    return this.http.get<CompanyDashboardMetrics>(`${this.apiUrl}/dashboard/metrics`);
  }

  /**
   * Get invoices list
   */
  getInvoices(
    page: number = 0,
    pageSize: number = 10,
    status?: string,
    search?: string
  ): Observable<InvoiceListResponse> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', pageSize.toString());

    if (status) {
      params = params.set('status', status);
    }

    if (search) {
      params = params.set('search', search);
    }

    return this.http.get<InvoiceListResponse>(`${this.apiUrl}/invoices`, { params });
  }

  /**
   * Get invoice by ID
   */
  getInvoiceById(invoiceId: number): Observable<Invoice> {
    return this.http.get<Invoice>(`${this.apiUrl}/invoices/${invoiceId}`);
  }

  /**
   * Get company profile
   */
  getCompanyProfile(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/profile`);
  }

  /**
   * Update company profile
   */
  updateCompanyProfile(profileData: {
    name: string;
    gstNumber?: string;
    contactEmail: string;
    contactPhone?: string;
  }): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/profile`, profileData);
  }

  /**
   * Mark invoice as paid
   */
  markInvoiceAsPaid(invoiceId: number, amountReceived: number, paymentDate: string): Observable<Invoice> {
    return this.http.post<Invoice>(`${this.apiUrl}/invoices/${invoiceId}/mark-paid`, {
      amountReceived,
      paymentDate
    });
  }

  /**
   * Get customers list
   */
  getCustomers(
    page: number = 0,
    pageSize: number = 10,
    search?: string
  ): Observable<CustomerListResponse> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', pageSize.toString());

    if (search) {
      params = params.set('search', search);
    }

    return this.http.get<CustomerListResponse>(`${this.apiUrl}/customers`, { params });
  }

  /**
   * Get customer by ID
   */
  getCustomerById(customerId: number): Observable<Customer> {
    return this.http.get<Customer>(`${this.apiUrl}/customers/${customerId}`);
  }

  /**
   * Get payments list
   */
  getPayments(
    page: number = 0,
    pageSize: number = 10,
    search?: string
  ): Observable<PaymentListResponse> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', pageSize.toString());

    if (search) {
      params = params.set('search', search);
    }

    return this.http.get<PaymentListResponse>(`${this.apiUrl}/payments`, { params });
  }

  /**
   * Upload invoice file
   * Creates a DRAFT invoice
   */
  uploadInvoiceFile(file: FormData, companyId: number, customerId?: number): Observable<{ invoiceId: number; message?: string }> {
    // Add companyId and optional customerId to FormData
    file.append('companyId', companyId.toString());
    if (customerId) {
      file.append('customerId', customerId.toString());
    }

    // Use the correct endpoint: /api/invoices/upload (not /api/company/invoices/upload)
    return this.http.post<{ invoiceId: number; message?: string }>(`${environment.apiUrl}/invoices/upload`, file);
  }

  /**
   * Confirm draft invoice
   * Updates invoice with provided data and changes status from DRAFT to PENDING (ACTIVE)
   */
  confirmInvoice(
    invoiceId: number,
    invoiceNumber: string,
    invoiceDate: string,
    dueDate: string,
    amount: number,
    customerId?: number
  ): Observable<Invoice> {
    return this.http.post<Invoice>(`${environment.apiUrl}/invoices/${invoiceId}/confirm`, {
      invoiceNumber,
      invoiceDate,
      dueDate,
      amount,
      customerId
    });
  }

  /**
   * Get draft invoices for review
   */
  getDraftInvoices(): Observable<Invoice[]> {
    const companyId = this.getCompanyIdFromToken();
    if (!companyId) {
      throw new Error('Company ID not found in token');
    }
    return this.http.get<Invoice[]>(`${environment.apiUrl}/invoices/drafts`, {
      params: { companyId: companyId.toString() }
    });
  }

  /**
   * Create invoice manually (without file upload)
   * Creates a DRAFT invoice with manually entered data
   */
  createInvoice(
    invoiceNumber: string,
    invoiceDate: string,
    dueDate: string | null,
    amount: number,
    customerId?: number
  ): Observable<{ invoiceId: number; message?: string }> {
    const companyId = this.getCompanyIdFromToken();
    if (!companyId) {
      throw new Error('Company ID not found in token');
    }

    return this.http.post<{ invoiceId: number; message?: string }>(
      `${environment.apiUrl}/invoices/create`,
      {
        invoiceNumber,
        invoiceDate,
        dueDate,
        amount,
        customerId
      },
      {
        params: { companyId: companyId.toString() }
      }
    );
  }

  /**
   * Get company ID from JWT token (helper method)
   */
  private getCompanyIdFromToken(): number | null {
    // This will be handled by the HTTP interceptor, but we can also extract here
    const token = localStorage.getItem('auth_token');
    if (!token) {
      return null;
    }
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.companyId || null;
    } catch {
      return null;
    }
  }
}

