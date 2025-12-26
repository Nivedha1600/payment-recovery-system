"""
Test script to run scheduler immediately (for testing)
Useful for testing without waiting for 9 AM
"""
import logging
import sys
from pathlib import Path
from datetime import datetime

# Add parent directory to path
sys.path.insert(0, str(Path(__file__).parent.parent))

from src.scheduler import create_scheduler
from src.reminder_service import ReminderService
from src.spring_boot_client import SpringBootClient
from config import config

# Setup logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)

logger = logging.getLogger(__name__)


def test_reminder_processing():
    """Test reminder processing immediately"""
    print("\n" + "=" * 60)
    print("TESTING REMINDER PROCESSING")
    print("=" * 60 + "\n")
    
    try:
        # Create API client
        api_client = SpringBootClient(
            base_url=config.java_api_base_url,
            api_key=config.java_api_key,
            timeout=config.java_api_timeout
        )
        
        # Create reminder service
        reminder_service = ReminderService(api_client)
        
        # Process reminders
        stats = reminder_service.process_reminders()
        
        print("\n" + "=" * 60)
        print("PROCESSING COMPLETE")
        print("=" * 60)
        print(f"Total invoices: {stats['total']}")
        print(f"Reminders sent: {stats['sent']}")
        print(f"Failed: {stats['failed']}")
        print(f"Skipped: {stats['skipped']}")
        print("=" * 60 + "\n")
        
    except Exception as e:
        logger.error(f"Error: {e}", exc_info=True)


if __name__ == '__main__':
    print("Reminder Scheduler Test")
    print("This will process reminders immediately (not waiting for 9 AM)")
    print()
    
    test_reminder_processing()

