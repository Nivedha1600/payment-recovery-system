/**
 * Dashboard metrics model for admin
 */
export interface PlatformMetrics {
  totalCompanies: number;
  activeCompanies: number;
  inactiveCompanies: number;
  totalUsers: number;
  totalInvoices: number;
  pendingInvoices: number;
  paidInvoices: number;
  totalRevenue: number;
  recentActivity: ActivityItem[];
}

export interface ActivityItem {
  id: number;
  type: 'COMPANY_CREATED' | 'COMPANY_ACTIVATED' | 'COMPANY_DEACTIVATED' | 'USER_CREATED' | 'INVOICE_CREATED';
  description: string;
  timestamp: string;
  companyName?: string;
}

