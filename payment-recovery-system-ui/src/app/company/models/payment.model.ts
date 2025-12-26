/**
 * Payment model for company users
 */
export interface Payment {
  id: number;
  invoiceId: number;
  invoiceNumber: string;
  amountReceived: number;
  paymentDate: string;
  customerName?: string;
}

export interface PaymentListResponse {
  payments: Payment[];
  total: number;
  page: number;
  pageSize: number;
}

