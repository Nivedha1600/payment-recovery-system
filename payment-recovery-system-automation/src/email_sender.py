"""
Email Sender Module
Mock implementation for sending email messages
"""
import logging
from typing import Dict, Optional, List
from datetime import datetime

logger = logging.getLogger(__name__)


class EmailSender:
    """Mock email message sender"""
    
    def __init__(self):
        """Initialize email sender"""
        self.sender_email = "noreply@paymentrecovery.com"
        self.sender_name = "Payment Recovery System"
    
    def send_email(
        self,
        to_email: str,
        subject: str,
        body: str,
        invoice_data: Optional[Dict] = None,
        cc: Optional[List[str]] = None,
        bcc: Optional[List[str]] = None
    ) -> bool:
        """
        Send email message (mock - prints message)
        
        Args:
            to_email: Recipient email address
            subject: Email subject
            body: Email body content
            invoice_data: Optional invoice data for logging
            cc: Optional CC recipients
            bcc: Optional BCC recipients
            
        Returns:
            True if email would be sent successfully
        """
        try:
            # Print email message
            print("\n" + "=" * 70)
            print("📧 EMAIL MESSAGE (MOCK)")
            print("=" * 70)
            print(f"Timestamp: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
            print(f"From: {self.sender_name} <{self.sender_email}>")
            print(f"To: {to_email}")
            
            if cc:
                print(f"CC: {', '.join(cc)}")
            if bcc:
                print(f"BCC: {', '.join(bcc)}")
            
            print(f"Subject: {subject}")
            
            if invoice_data:
                print(f"Invoice Number: {invoice_data.get('invoiceNumber', 'N/A')}")
                print(f"Customer: {invoice_data.get('customerName', 'N/A')}")
                print(f"Reminder Type: {invoice_data.get('reminderType', 'N/A')}")
                print(f"Due Date: {invoice_data.get('dueDate', 'N/A')}")
                amount = invoice_data.get('amount')
                if amount:
                    amount_str = f"${amount:,.2f}" if isinstance(amount, (int, float)) else str(amount)
                    print(f"Amount: {amount_str}")
            
            print("-" * 70)
            print("EMAIL BODY:")
            print(body)
            print("=" * 70 + "\n")
            
            logger.info(
                f"Sent email to {to_email} with subject '{subject}' "
                f"(Invoice: {invoice_data.get('invoiceNumber', 'N/A') if invoice_data else 'N/A'})"
            )
            
            return True
            
        except Exception as e:
            logger.error(f"Error sending email to {to_email}: {e}")
            return False
    
    def send_html_email(
        self,
        to_email: str,
        subject: str,
        html_body: str,
        text_body: Optional[str] = None,
        invoice_data: Optional[Dict] = None
    ) -> bool:
        """
        Send HTML email message (mock - prints message)
        
        Args:
            to_email: Recipient email address
            subject: Email subject
            html_body: HTML email body
            text_body: Optional plain text alternative
            invoice_data: Optional invoice data for logging
            
        Returns:
            True if email would be sent successfully
        """
        try:
            # Print email message
            print("\n" + "=" * 70)
            print("📧 HTML EMAIL MESSAGE (MOCK)")
            print("=" * 70)
            print(f"Timestamp: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
            print(f"From: {self.sender_name} <{self.sender_email}>")
            print(f"To: {to_email}")
            print(f"Subject: {subject}")
            print(f"Content-Type: text/html")
            
            if invoice_data:
                print(f"Invoice Number: {invoice_data.get('invoiceNumber', 'N/A')}")
                print(f"Customer: {invoice_data.get('customerName', 'N/A')}")
            
            print("-" * 70)
            print("HTML BODY:")
            print(html_body)
            
            if text_body:
                print("-" * 70)
                print("TEXT ALTERNATIVE:")
                print(text_body)
            
            print("=" * 70 + "\n")
            
            logger.info(
                f"Sent HTML email to {to_email} with subject '{subject}' "
                f"(Invoice: {invoice_data.get('invoiceNumber', 'N/A') if invoice_data else 'N/A'})"
            )
            
            return True
            
        except Exception as e:
            logger.error(f"Error sending HTML email to {to_email}: {e}")
            return False


# Convenience function
def send_email(
    to_email: str,
    subject: str,
    body: str,
    invoice_data: Optional[Dict] = None
) -> bool:
    """
    Convenience function to send email
    
    Args:
        to_email: Recipient email address
        subject: Email subject
        body: Email body content
        invoice_data: Optional invoice data
        
    Returns:
        True if email would be sent successfully
        
    Example:
        >>> send_email(
        ...     to_email='customer@example.com',
        ...     subject='Payment Reminder: INV-001',
        ...     body='Your invoice is due...',
        ...     invoice_data={'invoiceNumber': 'INV-001'}
        ... )
    """
    sender = EmailSender()
    return sender.send_email(to_email, subject, body, invoice_data)

