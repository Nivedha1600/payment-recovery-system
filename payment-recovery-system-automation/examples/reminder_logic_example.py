"""
Example usage of Reminder Logic
Demonstrates reminder type determination and duplicate prevention
"""
import logging
import sys
from pathlib import Path
from datetime import date, timedelta

# Add parent directory to path
sys.path.insert(0, str(Path(__file__).parent.parent))

from src.reminder_logic import ReminderLogic, ReminderType

# Setup logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)

logger = logging.getLogger(__name__)


def example_reminder_type_determination():
    """Example: Determine reminder type based on due date"""
    print("\n=== Example: Reminder Type Determination ===\n")
    
    logic = ReminderLogic()
    
    # Create test invoices with different due dates
    test_cases = [
        {
            'name': '5 days before due date',
            'invoice': {
                'id': 1,
                'invoiceNumber': 'INV-001',
                'dueDate': (date.today() + timedelta(days=5)).strftime('%Y-%m-%d')
            },
            'expected': ReminderType.GENTLE
        },
        {
            'name': 'Due today',
            'invoice': {
                'id': 2,
                'invoiceNumber': 'INV-002',
                'dueDate': date.today().strftime('%Y-%m-%d')
            },
            'expected': ReminderType.DUE
        },
        {
            'name': '7 days overdue',
            'invoice': {
                'id': 3,
                'invoiceNumber': 'INV-003',
                'dueDate': (date.today() - timedelta(days=7)).strftime('%Y-%m-%d')
            },
            'expected': ReminderType.FIRM
        },
        {
            'name': '15 days overdue',
            'invoice': {
                'id': 4,
                'invoiceNumber': 'INV-004',
                'dueDate': (date.today() - timedelta(days=15)).strftime('%Y-%m-%d')
            },
            'expected': ReminderType.ESCALATION
        },
        {
            'name': 'Other day (no reminder)',
            'invoice': {
                'id': 5,
                'invoiceNumber': 'INV-005',
                'dueDate': (date.today() - timedelta(days=3)).strftime('%Y-%m-%d')
            },
            'expected': None
        }
    ]
    
    for test_case in test_cases:
        invoice = test_case['invoice']
        expected = test_case['expected']
        
        reminder_type = logic.determine_reminder_type(invoice)
        
        status = "✓" if reminder_type == expected else "✗"
        print(f"{status} {test_case['name']}:")
        print(f"   Invoice: {invoice['invoiceNumber']}")
        print(f"   Due Date: {invoice['dueDate']}")
        print(f"   Reminder Type: {reminder_type}")
        print(f"   Expected: {expected}")
        print()


def example_duplicate_prevention():
    """Example: Prevent sending same reminder twice in one day"""
    print("\n=== Example: Duplicate Prevention ===\n")
    
    logic = ReminderLogic()
    invoice_id = 1
    reminder_type = ReminderType.GENTLE
    channel = 'EMAIL'
    
    # First attempt - should be allowed
    print("First attempt to send reminder:")
    should_send = logic.should_send_reminder(invoice_id, reminder_type, channel)
    print(f"  Should send: {should_send}")
    
    if should_send:
        print("  → Sending reminder...")
        logic.mark_reminder_sent(invoice_id, reminder_type, channel)
        print("  → Reminder sent and marked")
    
    print()
    
    # Second attempt - should be blocked
    print("Second attempt to send same reminder:")
    should_send = logic.should_send_reminder(invoice_id, reminder_type, channel)
    print(f"  Should send: {should_send}")
    print(f"  → Reminder blocked (already sent today)")
    print()
    
    # Different channel - should be allowed
    print("Attempt to send via different channel (WHATSAPP):")
    should_send = logic.should_send_reminder(invoice_id, reminder_type, 'WHATSAPP')
    print(f"  Should send: {should_send}")
    print(f"  → Different channel allowed")
    print()
    
    # Different reminder type - should be allowed
    print("Attempt to send different reminder type (DUE):")
    should_send = logic.should_send_reminder(invoice_id, ReminderType.DUE, channel)
    print(f"  Should send: {should_send}")
    print(f"  → Different reminder type allowed")


def example_complete_workflow():
    """Example: Complete reminder processing workflow"""
    print("\n=== Example: Complete Workflow ===\n")
    
    logic = ReminderLogic()
    
    # Simulate processing multiple invoices
    invoices = [
        {
            'id': 1,
            'invoiceNumber': 'INV-001',
            'dueDate': (date.today() + timedelta(days=5)).strftime('%Y-%m-%d'),
            'customerEmail': 'customer1@example.com'
        },
        {
            'id': 2,
            'invoiceNumber': 'INV-002',
            'dueDate': date.today().strftime('%Y-%m-%d'),
            'customerEmail': 'customer2@example.com'
        },
        {
            'id': 1,  # Same invoice ID - should be blocked on second attempt
            'invoiceNumber': 'INV-001',
            'dueDate': (date.today() + timedelta(days=5)).strftime('%Y-%m-%d'),
            'customerEmail': 'customer1@example.com'
        }
    ]
    
    for invoice in invoices:
        invoice_id = invoice['id']
        invoice_number = invoice['invoiceNumber']
        
        print(f"Processing invoice {invoice_number} (ID: {invoice_id})...")
        
        # Determine reminder type
        reminder_type = logic.determine_reminder_type(invoice)
        
        if not reminder_type:
            print(f"  → No reminder needed for this day")
            print()
            continue
        
        print(f"  → Reminder type: {reminder_type}")
        
        # Check if should send
        channel = 'EMAIL'
        should_send = logic.should_send_reminder(invoice_id, reminder_type, channel)
        
        if should_send:
            print(f"  → Sending {reminder_type} reminder via {channel}...")
            # Simulate sending
            print(f"  → Reminder sent successfully")
            logic.mark_reminder_sent(invoice_id, reminder_type, channel)
        else:
            print(f"  → Reminder already sent today, skipping")
        
        print()


if __name__ == '__main__':
    print("Reminder Logic - Example Usage")
    print("=" * 50)
    
    # Run examples
    example_reminder_type_determination()
    example_duplicate_prevention()
    example_complete_workflow()

