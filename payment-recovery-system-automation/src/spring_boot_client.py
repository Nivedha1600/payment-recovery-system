"""
Spring Boot API Client
Python module for calling Spring Boot REST APIs
"""
import logging
import time
from typing import Dict, List, Optional, Any
import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

logger = logging.getLogger(__name__)


class SpringBootClient:
    """
    Client for Spring Boot REST API
    Handles communication with the Java backend
    """
    
    def __init__(
        self,
        base_url: str,
        api_key: str,
        timeout: int = 30,
        max_retries: int = 3,
        retry_delay: float = 1.0
    ):
        """
        Initialize Spring Boot API client
        
        Args:
            base_url: Base URL of the Spring Boot API (e.g., 'http://localhost:8080')
            api_key: API key for authentication
            timeout: Request timeout in seconds
            max_retries: Maximum number of retry attempts
            retry_delay: Initial delay between retries in seconds
        """
        self.base_url = base_url.rstrip('/')
        self.api_key = api_key
        self.timeout = timeout
        self.max_retries = max_retries
        self.retry_delay = retry_delay
        
        # Create session with retry strategy
        self.session = requests.Session()
        
        # Configure retry strategy
        retry_strategy = Retry(
            total=max_retries,
            backoff_factor=retry_delay,
            status_forcelist=[429, 500, 502, 503, 504],
            allowed_methods=["GET", "POST", "PUT"]
        )
        adapter = HTTPAdapter(max_retries=retry_strategy)
        self.session.mount("http://", adapter)
        self.session.mount("https://", adapter)
        
        # Set default headers
        self.session.headers.update({
            'Authorization': f'Bearer {api_key}',
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        })
    
    def _make_request(
        self,
        method: str,
        endpoint: str,
        data: Optional[Dict] = None,
        params: Optional[Dict] = None
    ) -> Dict[str, Any]:
        """
        Make HTTP request to Spring Boot API
        
        Args:
            method: HTTP method (GET, POST, PUT, etc.)
            endpoint: API endpoint path (e.g., '/api/invoices/pending-for-reminder')
            data: Request body data (for POST/PUT)
            params: Query parameters (for GET)
            
        Returns:
            Response JSON as dictionary
            
        Raises:
            requests.exceptions.RequestException: On API errors
        """
        url = f"{self.base_url}{endpoint}"
        
        try:
            logger.debug(f"Making {method} request to {url}")
            
            response = self.session.request(
                method=method,
                url=url,
                json=data,
                params=params,
                timeout=self.timeout
            )
            
            # Raise exception for HTTP errors
            response.raise_for_status()
            
            # Return JSON if response has content, otherwise empty dict
            if response.content:
                return response.json()
            return {}
            
        except requests.exceptions.HTTPError as e:
            logger.error(
                f"HTTP error {e.response.status_code} for {method} {url}: {e.response.text}"
            )
            raise
        except requests.exceptions.ConnectionError as e:
            logger.error(f"Connection error for {method} {url}: {e}")
            raise
        except requests.exceptions.Timeout as e:
            logger.error(f"Timeout error for {method} {url}: {e}")
            raise
        except requests.exceptions.RequestException as e:
            logger.error(f"Request error for {method} {url}: {e}")
            raise
    
    def get_pending_invoices_for_reminder(self) -> List[Dict[str, Any]]:
        """
        Get all pending invoices for reminders
        
        Calls: GET /api/invoices/pending-for-reminder
        
        Returns:
            List of invoice reminder DTOs with fields:
            - id: Invoice ID
            - invoiceNumber: Invoice number
            - dueDate: Due date
            - amount: Invoice amount
            - companyId: Company ID
            - customerName: Customer name
            - customerEmail: Customer email
            - customerPhone: Customer phone
            
        Example:
            >>> client = SpringBootClient('http://localhost:8080', 'api-key')
            >>> invoices = client.get_pending_invoices_for_reminder()
            >>> print(f"Found {len(invoices)} pending invoices")
        """
        try:
            logger.info("Fetching pending invoices for reminders")
            response = self._make_request('GET', '/api/invoices/pending-for-reminder')
            
            # Handle both list response and wrapped response
            if isinstance(response, list):
                invoices = response
            else:
                invoices = response.get('data', []) if isinstance(response, dict) else []
            
            logger.info(f"Retrieved {len(invoices)} pending invoices")
            return invoices
            
        except Exception as e:
            logger.error(f"Error fetching pending invoices: {e}")
            raise
    
    def log_reminder(
        self,
        invoice_id: int,
        reminder_type: str,
        channel: str
    ) -> Dict[str, Any]:
        """
        Log a reminder that was sent
        
        Calls: POST /api/reminders/log
        
        Args:
            invoice_id: Invoice ID
            reminder_type: Type of reminder (GENTLE, DUE, FIRM, ESCALATION)
            channel: Channel used (EMAIL, WHATSAPP)
            
        Returns:
            Reminder log response with fields:
            - id: Reminder log ID
            - invoiceId: Invoice ID
            - reminderType: Reminder type
            - channel: Channel used
            - sentDate: When reminder was sent
            - createdAt: When log was created
            
        Example:
            >>> client = SpringBootClient('http://localhost:8080', 'api-key')
            >>> result = client.log_reminder(
            ...     invoice_id=1,
            ...     reminder_type='GENTLE',
            ...     channel='EMAIL'
            ... )
            >>> print(f"Reminder logged with ID: {result['id']}")
        """
        payload = {
            'invoiceId': invoice_id,
            'reminderType': reminder_type,
            'channel': channel
        }
        
        try:
            logger.info(
                f"Logging reminder for invoice {invoice_id}: "
                f"{reminder_type} via {channel}"
            )
            response = self._make_request('POST', '/api/reminders/log', data=payload)
            logger.info(f"Successfully logged reminder for invoice {invoice_id}")
            return response
            
        except Exception as e:
            logger.error(f"Error logging reminder for invoice {invoice_id}: {e}")
            raise


# Convenience function for quick usage
def create_client(
    base_url: str = 'http://localhost:8080',
    api_key: str = '',
    timeout: int = 30
) -> SpringBootClient:
    """
    Create a Spring Boot API client with default configuration
    
    Args:
        base_url: Base URL of the Spring Boot API
        api_key: API key for authentication
        timeout: Request timeout in seconds
        
    Returns:
        Configured SpringBootClient instance
        
    Example:
        >>> from src.spring_boot_client import create_client
        >>> client = create_client('http://localhost:8080', 'my-api-key')
        >>> invoices = client.get_pending_invoices_for_reminder()
    """
    return SpringBootClient(
        base_url=base_url,
        api_key=api_key,
        timeout=timeout
    )

