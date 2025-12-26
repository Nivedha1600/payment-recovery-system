"""
File type detection utility
"""
import os
from enum import Enum
from typing import Optional


class FileType(str, Enum):
    """Supported file types"""
    PDF = "PDF"
    IMAGE = "IMAGE"
    EXCEL = "EXCEL"
    DOC = "DOC"
    UNKNOWN = "UNKNOWN"


class FileTypeDetector:
    """Detects file type from file path or extension"""
    
    @staticmethod
    def detect_from_path(file_path: str) -> FileType:
        """
        Detect file type from file path
        
        Args:
            file_path: File path
            
        Returns:
            FileType enum
        """
        if not file_path:
            return FileType.UNKNOWN
        
        extension = FileTypeDetector._get_extension(file_path).lower()
        
        if extension == 'pdf':
            return FileType.PDF
        elif extension in ['png', 'jpg', 'jpeg', 'gif', 'bmp', 'webp']:
            return FileType.IMAGE
        elif extension in ['xls', 'xlsx']:
            return FileType.EXCEL
        elif extension in ['doc', 'docx']:
            return FileType.DOC
        else:
            return FileType.UNKNOWN
    
    @staticmethod
    def _get_extension(file_path: str) -> str:
        """Get file extension from path"""
        _, ext = os.path.splitext(file_path)
        return ext.lstrip('.')

