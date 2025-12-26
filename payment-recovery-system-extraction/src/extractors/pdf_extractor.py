"""
PDF invoice extractor using pdfplumber
"""
import logging
from typing import Dict, Optional
import pdfplumber
from pathlib import Path

logger = logging.getLogger(__name__)


class PDFExtractor:
    """Extracts data from PDF invoices"""
    
    def extract(self, file_path: str) -> Dict:
        """
        Extract invoice data from PDF file
        
        Args:
            file_path: Full path to PDF file
            
        Returns:
            Dictionary with extracted invoice data
        """
        logger.info(f"Extracting data from PDF: {file_path}")
        
        extracted_data = {}
        
        try:
            with pdfplumber.open(file_path) as pdf:
                # Extract text from all pages
                full_text = ""
                for page in pdf.pages:
                    page_text = page.extract_text()
                    if page_text:
                        full_text += page_text + "\n"
                
                logger.debug(f"Extracted text length: {len(full_text)} characters")
                
                # Extract tables (if any)
                tables = []
                for page in pdf.pages:
                    page_tables = page.extract_tables()
                    if page_tables:
                        tables.extend(page_tables)
                
                # Basic extraction logic (can be enhanced with NLP/ML)
                extracted_data = self._parse_text(full_text)
                
                # Add table data if found
                if tables:
                    extracted_data['lineItems'] = self._extract_line_items_from_tables(tables)
                
                logger.info(f"Successfully extracted data from PDF: {file_path}")
                
        except Exception as e:
            logger.error(f"Error extracting PDF: {file_path}", exc_info=True)
            raise
        
        return extracted_data
    
    def _parse_text(self, text: str) -> Dict:
        """
        Parse extracted text to find invoice data
        This is a basic implementation - can be enhanced with NLP/ML
        
        Args:
            text: Extracted text from PDF
            
        Returns:
            Dictionary with parsed data
        """
        data = {}
        
        # Look for invoice number patterns
        import re
        
        # Invoice number patterns
        invoice_number_patterns = [
            r'invoice\s*#?\s*:?\s*([A-Z0-9\-]+)',
            r'inv\s*#?\s*:?\s*([A-Z0-9\-]+)',
            r'invoice\s+number\s*:?\s*([A-Z0-9\-]+)',
        ]
        
        for pattern in invoice_number_patterns:
            match = re.search(pattern, text, re.IGNORECASE)
            if match:
                data['invoiceNumber'] = match.group(1).strip()
                break
        
        # Amount patterns
        amount_patterns = [
            r'total\s*:?\s*\$?\s*([\d,]+\.?\d*)',
            r'amount\s*:?\s*\$?\s*([\d,]+\.?\d*)',
            r'due\s*:?\s*\$?\s*([\d,]+\.?\d*)',
        ]
        
        for pattern in amount_patterns:
            match = re.search(pattern, text, re.IGNORECASE)
            if match:
                try:
                    amount_str = match.group(1).replace(',', '')
                    data['amount'] = float(amount_str)
                    break
                except ValueError:
                    continue
        
        # Date patterns
        date_patterns = [
            r'date\s*:?\s*(\d{1,2}[/-]\d{1,2}[/-]\d{2,4})',
            r'invoice\s+date\s*:?\s*(\d{1,2}[/-]\d{1,2}[/-]\d{2,4})',
        ]
        
        for pattern in date_patterns:
            match = re.search(pattern, text, re.IGNORECASE)
            if match:
                # Try to parse date (simplified)
                data['invoiceDate'] = match.group(1)
                break
        
        return data
    
    def _extract_line_items_from_tables(self, tables: list) -> list:
        """
        Extract line items from PDF tables
        
        Args:
            tables: List of extracted tables
            
        Returns:
            List of line items
        """
        line_items = []
        
        for table in tables:
            if not table or len(table) < 2:
                continue
            
            # Assume first row is header
            # Try to find description, quantity, price columns
            for row in table[1:]:
                if len(row) >= 3:
                    item = {
                        'description': str(row[0]) if row[0] else '',
                        'quantity': str(row[1]) if len(row) > 1 and row[1] else '',
                        'price': str(row[2]) if len(row) > 2 and row[2] else ''
                    }
                    line_items.append(item)
        
        return line_items

