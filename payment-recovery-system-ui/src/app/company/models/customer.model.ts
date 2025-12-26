/**
 * Customer model for company users
 */
export interface Customer {
  id: number;
  customerName: string;
  companyName?: string;
  phone?: string;
  email?: string;
  paymentTermsDays: number;
  totalInvoices?: number;
  totalAmount?: number;
  pendingAmount?: number;
}

export interface CustomerListResponse {
  customers: Customer[];
  total: number;
  page: number;
  pageSize: number;
}

