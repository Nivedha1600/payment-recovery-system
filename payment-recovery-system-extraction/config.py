"""
Configuration management for Invoice Extraction Service
"""
import os
from dataclasses import dataclass
from typing import Optional
from dotenv import load_dotenv

# Load environment variables
load_dotenv()


@dataclass
class Config:
    """Application configuration"""
    
    # Java API Configuration
    java_api_base_url: str = os.getenv('JAVA_API_BASE_URL', 'http://localhost:8080')
    java_api_key: str = os.getenv('JAVA_API_KEY', '')
    java_api_timeout: int = int(os.getenv('JAVA_API_TIMEOUT', '30'))
    
    # File Storage Configuration
    java_file_base_path: str = os.getenv('JAVA_FILE_BASE_PATH', 'uploads/invoices')
    
    # Extraction Configuration
    extraction_timeout: int = int(os.getenv('EXTRACTION_TIMEOUT', '300'))
    
    # Logging
    log_level: str = os.getenv('LOG_LEVEL', 'INFO')
    
    # Application
    app_name: str = 'invoice-extraction-service'
    app_version: str = '1.0.0'
    environment: str = os.getenv('ENVIRONMENT', 'development')
    
    def validate(self) -> None:
        """Validate configuration"""
        if not self.java_api_base_url:
            raise ValueError("JAVA_API_BASE_URL is required")


# Global configuration instance
config = Config()

