"""
Configuration management for Payment Recovery Automation Service
Loads configuration from environment variables and config file
"""
import os
from dataclasses import dataclass
from typing import Optional
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()


@dataclass
class Config:
    """Application configuration"""
    
    # Java API Configuration
    java_api_base_url: str = os.getenv('JAVA_API_BASE_URL', 'http://localhost:8080')
    java_api_key: str = os.getenv('JAVA_API_KEY', '')
    java_api_timeout: int = int(os.getenv('JAVA_API_TIMEOUT', '30'))
    
    # Retry Configuration
    max_retries: int = int(os.getenv('MAX_RETRIES', '3'))
    retry_delay: float = float(os.getenv('RETRY_DELAY', '1.0'))
    retry_backoff_factor: float = float(os.getenv('RETRY_BACKOFF_FACTOR', '2.0'))
    
    # Scheduler Configuration
    scheduler_timezone: str = os.getenv('SCHEDULER_TIMEZONE', 'UTC')
    reminder_check_interval: int = int(os.getenv('REMINDER_CHECK_INTERVAL', '3600'))  # seconds
    
    # Reminder Configuration
    reminder_batch_size: int = int(os.getenv('REMINDER_BATCH_SIZE', '100'))
    enable_email_reminders: bool = os.getenv('ENABLE_EMAIL_REMINDERS', 'true').lower() == 'true'
    enable_whatsapp_reminders: bool = os.getenv('ENABLE_WHATSAPP_REMINDERS', 'true').lower() == 'true'
    
    # Email Configuration (if sending emails directly from Python)
    smtp_host: Optional[str] = os.getenv('SMTP_HOST')
    smtp_port: int = int(os.getenv('SMTP_PORT', '587'))
    smtp_username: Optional[str] = os.getenv('SMTP_USERNAME')
    smtp_password: Optional[str] = os.getenv('SMTP_PASSWORD')
    smtp_from_email: Optional[str] = os.getenv('SMTP_FROM_EMAIL')
    
    # WhatsApp Configuration (if sending WhatsApp directly from Python)
    whatsapp_api_url: Optional[str] = os.getenv('WHATSAPP_API_URL')
    whatsapp_api_key: Optional[str] = os.getenv('WHATSAPP_API_KEY')
    
    # Logging Configuration
    log_level: str = os.getenv('LOG_LEVEL', 'INFO')
    log_format: str = os.getenv('LOG_FORMAT', 'json')  # json or text
    
    # Application Configuration
    app_name: str = 'payment-recovery-automation'
    app_version: str = '1.0.0'
    environment: str = os.getenv('ENVIRONMENT', 'development')
    
    def validate(self) -> None:
        """Validate configuration"""
        if not self.java_api_base_url:
            raise ValueError("JAVA_API_BASE_URL is required")
        if not self.java_api_key:
            raise ValueError("JAVA_API_KEY is required")


# Global configuration instance
config = Config()

