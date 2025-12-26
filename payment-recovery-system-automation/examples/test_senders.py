"""
Test script for WhatsApp and Email senders
Demonstrates mock message sending
"""
import logging
import sys
from pathlib import Path

# Add parent directory to path
sys.path.insert(0, str(Path(__file__).parent.parent))

from src.whatsapp_sender import WhatsAppSender, send_whatsapp_message
from src.email_sender import EmailSender, send_email

# Setup logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)

logger = logging.getLogger(__name__)


def test_whatsapp_sender():
    """Test WhatsApp sender"""
    print("\n" + "=" * 70)
    print("TESTING WHATSAPP SENDER")
    print("=" * 70 + "\n")
    
    sender = WhatsAppSender()
    
    invoice_data = {
        'invoiceNumber': 'INV-2024-001',
        'customerName': 'Acme Corporation',
        'reminderType': 'GENTLE',
        'dueDate': '2024-01-20',
        'amount': 5000.00
    }
    
    message = (
        "Hello Acme Corporation,\n\n"
        "👋 Gentle reminder: Your invoice INV-2024-001 is due in 5 days (Due: 2024-01-20).\n\n"
        "Amount: $5,000.00\n\n"
        "Please ensure payment is made on time.\n\n"
        "Thank you!"
    )
    
    result = sender.send_message(
        phone_number='+1-555-0123',
        message=message,
        invoice_data=invoice_data
    )
    
    print(f"\nWhatsApp send result: {result}")


def test_email_sender():
    """Test Email sender"""
    print("\n" + "=" * 70)
    print("TESTING EMAIL SENDER")
    print("=" * 70 + "\n")
    
    sender = EmailSender()
    
    invoice_data = {
        'invoiceNumber': 'INV-2024-001',
        'customerName': 'Acme Corporation',
        'reminderType': 'DUE',
        'dueDate': '2024-01-15',
        'amount': 5000.00
    }
    
    subject = "Payment Due Today - Invoice INV-2024-001"
    body = (
        "Dear Acme Corporation,\n\n"
        "This is to remind you that your invoice INV-2024-001 is due today.\n\n"
        "Invoice Details:\n"
        "  Invoice Number: INV-2024-001\n"
        "  Due Date: 2024-01-15\n"
        "  Amount: $5,000.00\n\n"
        "Please make payment today to avoid any late fees or service interruptions.\n\n"
        "If you have already made the payment, please disregard this message.\n\n"
        "Thank you for your prompt attention to this matter.\n\n"
        "Best regards,\n"
        "Payment Recovery System"
    )
    
    result = sender.send_email(
        to_email='billing@acme.com',
        subject=subject,
        body=body,
        invoice_data=invoice_data
    )
    
    print(f"\nEmail send result: {result}")


def test_convenience_functions():
    """Test convenience functions"""
    print("\n" + "=" * 70)
    print("TESTING CONVENIENCE FUNCTIONS")
    print("=" * 70 + "\n")
    
    # Test WhatsApp convenience function
    send_whatsapp_message(
        phone_number='+1-555-9999',
        message='Test WhatsApp message',
        invoice_data={'invoiceNumber': 'INV-TEST-001'}
    )
    
    # Test Email convenience function
    send_email(
        to_email='test@example.com',
        subject='Test Email',
        body='This is a test email message.',
        invoice_data={'invoiceNumber': 'INV-TEST-001'}
    )


if __name__ == '__main__':
    print("WhatsApp and Email Sender Test")
    print("=" * 70)
    
    test_whatsapp_sender()
    test_email_sender()
    test_convenience_functions()
    
    print("\n" + "=" * 70)
    print("ALL TESTS COMPLETE")
    print("=" * 70)

