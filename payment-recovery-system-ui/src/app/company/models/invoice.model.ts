/**
 * Invoice model for company users
 */
export interface Invoice {
  id: number;
  invoiceNumber: string;
  invoiceDate: string;
  dueDate: string;
  amount: number;
  status: 'DRAFT' | 'PENDING' | 'PARTIAL' | 'PAID';
  customerName?: string;
  customerId?: number;
  isOverdue?: boolean;
  daysOverdue?: number;
}

export interface InvoiceListResponse {
  invoices: Invoice[];
  total: number;
  page: number;
  pageSize: number;
}

