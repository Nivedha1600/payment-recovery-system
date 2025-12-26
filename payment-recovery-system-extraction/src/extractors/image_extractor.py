"""
Image invoice extractor (mock implementation)
For now returns mock data, can be enhanced with OCR (Tesseract)
"""
import logging
from typing import Dict
from pathlib import Path

logger = logging.getLogger(__name__)


class ImageExtractor:
    """Extracts data from image invoices"""
    
    def extract(self, file_path: str) -> Dict:
        """
        Extract invoice data from image file
        Currently returns mock data
        
        Args:
            file_path: Full path to image file
            
        Returns:
            Dictionary with extracted invoice data (mock)
        """
        logger.info(f"Extracting data from image: {file_path}")
        
        # TODO: Implement OCR using Tesseract or cloud OCR service
        # For now, return mock data
        
        logger.warning("Image extraction not fully implemented, returning mock data")
        
        # Mock extracted data
        extracted_data = {
            'invoiceNumber': 'INV-IMG-001',
            'invoiceDate': '2024-01-15',
            'dueDate': '2024-02-15',
            'amount': 1000.00,
            'customerName': 'Customer from Image',
            'totalAmount': 1000.00,
            'currency': 'USD',
            'notes': 'Extracted from image (mock data - OCR not implemented)'
        }
        
        logger.info(f"Returning mock data for image: {file_path}")
        
        return extracted_data
    
    def _extract_with_ocr(self, file_path: str) -> Dict:
        """
        Extract using OCR (Tesseract) - not implemented yet
        
        Args:
            file_path: Full path to image file
            
        Returns:
            Dictionary with extracted data
        """
        # TODO: Implement OCR extraction
        # Example:
        # from PIL import Image
        # import pytesseract
        # 
        # image = Image.open(file_path)
        # text = pytesseract.image_to_string(image)
        # # Parse text similar to PDF extractor
        # return parsed_data
        
        return {}

