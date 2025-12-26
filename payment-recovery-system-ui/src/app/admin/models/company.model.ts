/**
 * Company model for admin management
 */
export interface Company {
  id: number;
  name: string;
  gstNumber?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt?: string;
  userCount?: number;
  invoiceCount?: number;
}

export interface CompanyListResponse {
  companies: Company[];
  total: number;
  page: number;
  pageSize: number;
}

export interface CompanyStatusUpdate {
  companyId: number;
  isActive: boolean;
}

