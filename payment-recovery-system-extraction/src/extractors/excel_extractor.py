"""
Excel invoice extractor using pandas
"""
import logging
from typing import Dict, Optional
import pandas as pd
from pathlib import Path

logger = logging.getLogger(__name__)


class ExcelExtractor:
    """Extracts data from Excel invoice files"""
    
    def extract(self, file_path: str) -> Dict:
        """
        Extract invoice data from Excel file
        
        Args:
            file_path: Full path to Excel file
            
        Returns:
            Dictionary with extracted invoice data
        """
        logger.info(f"Extracting data from Excel: {file_path}")
        
        extracted_data = {}
        
        try:
            # Read Excel file
            # Try to read all sheets
            excel_file = pd.ExcelFile(file_path)
            
            # Read first sheet (usually contains invoice data)
            df = pd.read_excel(excel_file, sheet_name=0)
            
            logger.debug(f"Excel file has {len(df)} rows and {len(df.columns)} columns")
            
            # Extract data from DataFrame
            extracted_data = self._extract_from_dataframe(df)
            
            # Try to extract line items
            line_items = self._extract_line_items(df)
            if line_items:
                extracted_data['lineItems'] = line_items
            
            logger.info(f"Successfully extracted data from Excel: {file_path}")
            
        except Exception as e:
            logger.error(f"Error extracting Excel: {file_path}", exc_info=True)
            raise
        
        return extracted_data
    
    def _extract_from_dataframe(self, df: pd.DataFrame) -> Dict:
        """
        Extract invoice data from DataFrame
        
        Args:
            df: Pandas DataFrame
            
        Returns:
            Dictionary with extracted data
        """
        data = {}
        
        # Convert DataFrame to dictionary for easier searching
        df_dict = df.to_dict('records')
        
        # Look for common invoice fields
        for row in df_dict:
            for key, value in row.items():
                if pd.isna(value):
                    continue
                
                key_lower = str(key).lower()
                value_str = str(value)
                
                # Invoice number
                if 'invoice' in key_lower and 'number' in key_lower:
                    data['invoiceNumber'] = value_str
                
                # Invoice date
                if 'invoice' in key_lower and 'date' in key_lower:
                    try:
                        data['invoiceDate'] = pd.to_datetime(value).date().isoformat()
                    except:
                        data['invoiceDate'] = value_str
                
                # Due date
                if 'due' in key_lower and 'date' in key_lower:
                    try:
                        data['dueDate'] = pd.to_datetime(value).date().isoformat()
                    except:
                        data['dueDate'] = value_str
                
                # Amount
                if 'amount' in key_lower or 'total' in key_lower:
                    try:
                        amount = float(value)
                        if 'total' in key_lower:
                            data['totalAmount'] = amount
                            data['amount'] = amount
                        else:
                            data['amount'] = amount
                    except:
                        pass
                
                # Customer name
                if 'customer' in key_lower and 'name' in key_lower:
                    data['customerName'] = value_str
                
                # Customer email
                if 'customer' in key_lower and 'email' in key_lower:
                    data['customerEmail'] = value_str
        
        return data
    
    def _extract_line_items(self, df: pd.DataFrame) -> list:
        """
        Extract line items from DataFrame
        
        Args:
            df: Pandas DataFrame
            
        Returns:
            List of line items
        """
        line_items = []
        
        # Look for rows that might be line items
        # Usually have description, quantity, price columns
        for _, row in df.iterrows():
            item = {}
            
            for col in df.columns:
                col_lower = str(col).lower()
                value = row[col]
                
                if pd.isna(value):
                    continue
                
                if 'description' in col_lower or 'item' in col_lower:
                    item['description'] = str(value)
                elif 'quantity' in col_lower or 'qty' in col_lower:
                    item['quantity'] = str(value)
                elif 'price' in col_lower or 'rate' in col_lower:
                    item['price'] = str(value)
                elif 'amount' in col_lower or 'total' in col_lower:
                    item['amount'] = str(value)
            
            if item:
                line_items.append(item)
        
        return line_items

