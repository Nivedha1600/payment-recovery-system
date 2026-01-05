/**
 * Invoice model for company users
 */
export interface Invoice {
  id: number;
  invoiceNumber: string | null;
  invoiceDate: string | null;
  dueDate: string | null;
  amount: number | null;
  status: 'DRAFT' | 'PENDING' | 'PARTIAL' | 'PAID';
  customerName?: string | null;
  customerId?: number | null;
  customer?: {
    id: number;
    customerName: string;
    companyName?: string;
    email?: string;
    phone?: string;
  } | null;
  isOverdue?: boolean;
  daysOverdue?: number;
}

export interface InvoiceListResponse {
  invoices: Invoice[];
  total: number;
  page: number;
  pageSize: number;
}

