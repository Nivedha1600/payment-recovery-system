/**
 * Company dashboard metrics model
 */
export interface CompanyDashboardMetrics {
  totalInvoices: number;
  pendingAmount: number;
  overdueAmount: number;
  moneyRecoveredThisMonth: number;
  pendingInvoices: number;
  paidInvoices: number;
  overdueInvoices: number;
}

