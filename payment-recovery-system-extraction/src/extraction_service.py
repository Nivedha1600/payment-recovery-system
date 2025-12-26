"""
Invoice extraction service
Orchestrates extraction from different file types
"""
import logging
import os
from typing import Dict, Optional
from pathlib import Path

from src.file_type_detector import FileTypeDetector, FileType
from src.extractors.pdf_extractor import PDFExtractor
from src.extractors.image_extractor import ImageExtractor
from src.extractors.excel_extractor import ExcelExtractor
from config import config

logger = logging.getLogger(__name__)


class ExtractionService:
    """Service for extracting invoice data from files"""
    
    def __init__(self):
        self.file_type_detector = FileTypeDetector()
        self.pdf_extractor = PDFExtractor()
        self.image_extractor = ImageExtractor()
        self.excel_extractor = ExcelExtractor()
    
    def extract_invoice_data(self, file_path: str) -> Dict:
        """
        Extract invoice data from file based on file type
        
        Args:
            file_path: Relative file path from upload directory
            
        Returns:
            Dictionary with extracted invoice data
        """
        # Get full file path
        full_path = self._get_full_file_path(file_path)
        
        if not os.path.exists(full_path):
            raise FileNotFoundError(f"File not found: {full_path}")
        
        # Detect file type
        file_type = self.file_type_detector.detect_from_path(full_path)
        
        logger.info(f"Detected file type: {file_type} for file: {file_path}")
        
        # Extract based on file type
        if file_type == FileType.PDF:
            extracted_data = self.pdf_extractor.extract(full_path)
        elif file_type == FileType.IMAGE:
            extracted_data = self.image_extractor.extract(full_path)
        elif file_type == FileType.EXCEL:
            extracted_data = self.excel_extractor.extract(full_path)
        elif file_type == FileType.DOC:
            # DOC extraction not implemented yet
            logger.warning(f"DOC extraction not implemented for: {file_path}")
            extracted_data = {'notes': 'DOC extraction not implemented'}
        else:
            raise ValueError(f"Unsupported file type: {file_type}")
        
        logger.info(f"Extraction completed for file: {file_path}")
        
        return extracted_data
    
    def _get_full_file_path(self, relative_path: str) -> str:
        """
        Get full file path from relative path
        
        Args:
            relative_path: Relative path from upload directory
            
        Returns:
            Full file path
        """
        # Construct full path
        # relative_path format: companyId/YYYY/MM/DD/filename
        # base_path: uploads/invoices
        full_path = os.path.join(config.java_file_base_path, relative_path)
        
        # Normalize path
        full_path = os.path.normpath(full_path)
        
        return full_path

