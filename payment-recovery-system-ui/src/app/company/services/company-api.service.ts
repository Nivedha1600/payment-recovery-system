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
   * Get draft invoices
   */
  getDraftInvoices(
    page: number = 0,
    pageSize: number = 10
  ): Observable<InvoiceListResponse> {
    return this.getInvoices(page, pageSize, 'DRAFT');
  }

  /**
   * Get invoice by ID
   */
  getInvoiceById(invoiceId: number): Observable<Invoice> {
    return this.http.get<Invoice>(`${this.apiUrl}/invoices/${invoiceId}`);
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
   */
  uploadInvoiceFile(file: FormData, companyId: number, customerId?: number): Observable<{ invoiceId: number }> {
    // Add companyId and optional customerId to FormData
    file.append('companyId', companyId.toString());
    if (customerId) {
      file.append('customerId', customerId.toString());
    }

    return this.http.post<{ invoiceId: number }>(`${this.apiUrl}/invoices/upload`, file);
  }

  /**
   * Activate draft invoice
   * Updates invoice with provided data and changes status from DRAFT to PENDING
   */
  activateDraftInvoice(
    invoiceId: number,
    invoiceNumber: string,
    invoiceDate: string,
    dueDate: string | null,
    amount: number
  ): Observable<Invoice> {
    return this.http.patch<Invoice>(`${this.apiUrl}/invoices/${invoiceId}/activate`, {
      invoiceNumber,
      invoiceDate,
      dueDate,
      amount
    });
  }
}

