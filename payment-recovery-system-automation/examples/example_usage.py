"""
Example usage of Spring Boot API Client
Demonstrates how to use the client to call Spring Boot APIs
"""
import logging
import sys
from pathlib import Path

# Add parent directory to path
sys.path.insert(0, str(Path(__file__).parent.parent))

from src.spring_boot_client import SpringBootClient, create_client

# Setup logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)

logger = logging.getLogger(__name__)


def example_get_pending_invoices():
    """Example: Get pending invoices for reminders"""
    print("\n=== Example: Get Pending Invoices ===\n")
    
    # Create client
    client = create_client(
        base_url='http://localhost:8080',
        api_key='your-api-key-here'
    )
    
    try:
        # Get pending invoices
        invoices = client.get_pending_invoices_for_reminder()
        
        print(f"Found {len(invoices)} pending invoices:\n")
        
        # Display invoice details
        for invoice in invoices:
            print(f"Invoice ID: {invoice.get('id')}")
            print(f"  Invoice Number: {invoice.get('invoiceNumber')}")
            print(f"  Due Date: {invoice.get('dueDate')}")
            print(f"  Amount: {invoice.get('amount')}")
            print(f"  Customer: {invoice.get('customerName')}")
            print(f"  Email: {invoice.get('customerEmail')}")
            print(f"  Phone: {invoice.get('customerPhone')}")
            print()
            
    except Exception as e:
        logger.error(f"Error: {e}")


def example_log_reminder():
    """Example: Log a reminder"""
    print("\n=== Example: Log Reminder ===\n")
    
    # Create client
    client = create_client(
        base_url='http://localhost:8080',
        api_key='your-api-key-here'
    )
    
    try:
        # Log a reminder
        result = client.log_reminder(
            invoice_id=1,
            reminder_type='GENTLE',
            channel='EMAIL'
        )
        
        print("Reminder logged successfully:")
        print(f"  Reminder Log ID: {result.get('id')}")
        print(f"  Invoice ID: {result.get('invoiceId')}")
        print(f"  Reminder Type: {result.get('reminderType')}")
        print(f"  Channel: {result.get('channel')}")
        print(f"  Sent Date: {result.get('sentDate')}")
        
    except Exception as e:
        logger.error(f"Error: {e}")


def example_complete_workflow():
    """Example: Complete workflow - get invoices and log reminders"""
    print("\n=== Example: Complete Workflow ===\n")
    
    # Create client
    client = create_client(
        base_url='http://localhost:8080',
        api_key='your-api-key-here'
    )
    
    try:
        # Step 1: Get pending invoices
        print("Step 1: Fetching pending invoices...")
        invoices = client.get_pending_invoices_for_reminder()
        print(f"Found {len(invoices)} pending invoices\n")
        
        # Step 2: Process each invoice and log reminder
        for invoice in invoices[:3]:  # Process first 3 for example
            invoice_id = invoice.get('id')
            invoice_number = invoice.get('invoiceNumber')
            
            print(f"Processing invoice {invoice_number} (ID: {invoice_id})...")
            
            # Determine reminder type (simplified logic)
            reminder_type = 'GENTLE'  # In real scenario, use business logic
            channel = 'EMAIL' if invoice.get('customerEmail') else 'WHATSAPP'
            
            # Log the reminder
            result = client.log_reminder(
                invoice_id=invoice_id,
                reminder_type=reminder_type,
                channel=channel
            )
            
            print(f"  ✓ Reminder logged (ID: {result.get('id')})\n")
        
        print("Workflow completed successfully!")
        
    except Exception as e:
        logger.error(f"Error in workflow: {e}")


if __name__ == '__main__':
    print("Spring Boot API Client - Example Usage")
    print("=" * 50)
    
    # Uncomment the example you want to run:
    
    # example_get_pending_invoices()
    # example_log_reminder()
    example_complete_workflow()

