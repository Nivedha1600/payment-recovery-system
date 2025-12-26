/**
 * Extracted invoice data model
 * Represents data extracted from invoice files
 */
export interface ExtractedInvoiceData {
  invoiceNumber?: string;
  invoiceDate?: string;
  dueDate?: string;
  amount?: number;
  customerName?: string;
  customerEmail?: string;
  customerPhone?: string;
  lineItems?: LineItem[];
  taxAmount?: number;
  totalAmount?: number;
  currency?: string;
  notes?: string;
  additionalData?: { [key: string]: any };
}

export interface LineItem {
  description: string;
  quantity: number;
  unitPrice: number;
  total: number;
}

export interface DraftInvoice {
  id: number;
  invoiceNumber?: string;
  invoiceDate?: string;
  dueDate?: string;
  amount?: number;
  customerId?: number;
  customerName?: string;
  filePath?: string;
  status: 'DRAFT';
  extractedData?: ExtractedInvoiceData;
}

