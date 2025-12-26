"""
API Client for Java Backend
Handles all communication with the Java REST API
"""
import logging
import time
from typing import Dict, List, Optional, Any
from datetime import datetime

import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

from config import config

logger = logging.getLogger(__name__)


class AutomationApiClient:
    """Client for Java Automation API"""
    
    def __init__(self, base_url: str = None, api_key: str = None, timeout: int = None):
        self.base_url = (base_url or config.java_api_base_url).rstrip('/')
        self.api_key = api_key or config.java_api_key
        self.timeout = timeout or config.java_api_timeout
        
        # Create session with retry strategy
        self.session = requests.Session()
        retry_strategy = Retry(
            total=config.max_retries,
            backoff_factor=config.retry_backoff_factor,
            status_forcelist=[429, 500, 502, 503, 504],
            allowed_methods=["GET", "POST", "PUT"]
        )
        adapter = HTTPAdapter(max_retries=retry_strategy)
        self.session.mount("http://", adapter)
        self.session.mount("https://", adapter)
        
        # Set default headers
        self.session.headers.update({
            'Authorization': f'Bearer {self.api_key}',
            'Content-Type': 'application/json',
            'X-Service-Name': 'python-automation'
        })
    
    def _request(
        self, 
        method: str, 
        endpoint: str, 
        **kwargs
    ) -> Dict[str, Any]:
        """
        Make API request with error handling
        
        Args:
            method: HTTP method (GET, POST, PUT, etc.)
            endpoint: API endpoint path
            **kwargs: Additional arguments for requests
            
        Returns:
            Response JSON as dictionary
            
        Raises:
            requests.exceptions.RequestException: On API errors
        """
        url = f"{self.base_url}{endpoint}"
        
        try:
            logger.debug(f"Making {method} request to {url}")
            response = self.session.request(
                method, 
                url, 
                timeout=self.timeout,
                **kwargs
            )
            response.raise_for_status()
            return response.json() if response.content else {}
        except requests.exceptions.HTTPError as e:
            logger.error(f"HTTP error {e.response.status_code} for {method} {url}: {e}")
            raise
        except requests.exceptions.RequestException as e:
            logger.error(f"Request failed for {method} {url}: {e}")
            raise
    
    def get_pending_invoices_for_reminders(self) -> List[Dict[str, Any]]:
        """
        Get all pending invoices for reminders
        
        Returns:
            List of invoice reminder DTOs
        """
        try:
            response = self._request('GET', '/api/invoices/pending-for-reminder')
            invoices = response if isinstance(response, list) else response.get('data', [])
            logger.info(f"Retrieved {len(invoices)} pending invoices for reminders")
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
        
        Args:
            invoice_id: Invoice ID
            reminder_type: Type of reminder (GENTLE, DUE, FIRM, ESCALATION)
            channel: Channel used (EMAIL, WHATSAPP)
            
        Returns:
            Reminder log response
        """
        payload = {
            'invoiceId': invoice_id,
            'reminderType': reminder_type,
            'channel': channel
        }
        
        try:
            response = self._request('POST', '/api/reminders/log', json=payload)
            logger.info(f"Logged reminder for invoice {invoice_id}: {reminder_type} via {channel}")
            return response
        except Exception as e:
            logger.error(f"Error logging reminder for invoice {invoice_id}: {e}")
            raise
    
    def mark_invoice_as_paid(
        self, 
        invoice_id: int, 
        amount_received: float, 
        payment_date: str
    ) -> Dict[str, Any]:
        """
        Mark an invoice as paid
        
        Args:
            invoice_id: Invoice ID
            amount_received: Amount received
            payment_date: Payment date (YYYY-MM-DD format)
            
        Returns:
            Updated invoice response
        """
        payload = {
            'amountReceived': amount_received,
            'paymentDate': payment_date
        }
        
        try:
            response = self._request(
                'POST', 
                f'/api/invoices/{invoice_id}/mark-paid', 
                json=payload
            )
            logger.info(f"Marked invoice {invoice_id} as paid")
            return response
        except Exception as e:
            logger.error(f"Error marking invoice {invoice_id} as paid: {e}")
            raise

