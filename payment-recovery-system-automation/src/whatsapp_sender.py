"""
WhatsApp Sender Module
Mock implementation for sending WhatsApp messages
"""
import logging
from typing import Dict, Optional
from datetime import datetime

logger = logging.getLogger(__name__)


class WhatsAppSender:
    """Mock WhatsApp message sender"""
    
    def __init__(self):
        """Initialize WhatsApp sender"""
        self.sender_name = "Payment Recovery System"
    
    def send_message(
        self,
        phone_number: str,
        message: str,
        invoice_data: Optional[Dict] = None
    ) -> bool:
        """
        Send WhatsApp message (mock - prints message)
        
        Args:
            phone_number: Recipient phone number
            message: Message content
            invoice_data: Optional invoice data for logging
            
        Returns:
            True if message would be sent successfully
        """
        try:
            # Format phone number for display
            formatted_phone = self._format_phone_number(phone_number)
            
            # Print WhatsApp message
            print("\n" + "=" * 70)
            print("📱 WHATSAPP MESSAGE (MOCK)")
            print("=" * 70)
            print(f"Timestamp: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
            print(f"From: {self.sender_name}")
            print(f"To: {formatted_phone}")
            
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
            print("MESSAGE:")
            print(message)
            print("=" * 70 + "\n")
            
            logger.info(
                f"Sent WhatsApp message to {formatted_phone} "
                f"(Invoice: {invoice_data.get('invoiceNumber', 'N/A') if invoice_data else 'N/A'})"
            )
            
            return True
            
        except Exception as e:
            logger.error(f"Error sending WhatsApp message to {phone_number}: {e}")
            return False
    
    def _format_phone_number(self, phone_number: str) -> str:
        """
        Format phone number for display
        
        Args:
            phone_number: Raw phone number
            
        Returns:
            Formatted phone number
        """
        # Remove common separators
        cleaned = phone_number.replace(' ', '').replace('-', '').replace('(', '').replace(')', '')
        
        # Add formatting if it looks like a phone number
        if cleaned.startswith('+'):
            # International format: +1234567890 -> +1 (234) 567-890
            if len(cleaned) == 12:  # +1 + 10 digits
                return f"{cleaned[:2]} ({cleaned[2:5]}) {cleaned[5:8]}-{cleaned[8:]}"
            elif len(cleaned) == 13:  # +91 + 10 digits
                return f"{cleaned[:3]} {cleaned[3:8]} {cleaned[8:]}"
        
        # Return as-is if can't format
        return phone_number


# Convenience function
def send_whatsapp_message(
    phone_number: str,
    message: str,
    invoice_data: Optional[Dict] = None
) -> bool:
    """
    Convenience function to send WhatsApp message
    
    Args:
        phone_number: Recipient phone number
        message: Message content
        invoice_data: Optional invoice data
        
    Returns:
        True if message would be sent successfully
        
    Example:
        >>> send_whatsapp_message(
        ...     phone_number='+1234567890',
        ...     message='Payment reminder for invoice INV-001',
        ...     invoice_data={'invoiceNumber': 'INV-001'}
        ... )
    """
    sender = WhatsAppSender()
    return sender.send_message(phone_number, message, invoice_data)

