"""
Reminder Logic Module
Determines reminder type based on due date and manages reminder tracking
"""
import logging
from datetime import datetime, date, timedelta
from typing import Dict, Optional, Set, Tuple
from collections import defaultdict

logger = logging.getLogger(__name__)


class ReminderType:
    """Reminder type constants"""
    GENTLE = 'GENTLE'
    DUE = 'DUE'
    FIRM = 'FIRM'
    ESCALATION = 'ESCALATION'


class ReminderLogic:
    """
    Logic for determining reminder type based on due date
    and tracking sent reminders to prevent duplicates
    """
    
    def __init__(self):
        """Initialize reminder logic with tracking"""
        # Track reminders sent today: {invoice_id: {reminder_type, channel}}
        self.sent_today: Dict[int, Set[Tuple[str, str]]] = defaultdict(set)
        self.last_reset_date: Optional[date] = None
    
    def _reset_daily_tracking(self):
        """Reset daily tracking if it's a new day"""
        today = date.today()
        if self.last_reset_date != today:
            logger.debug(f"Resetting daily reminder tracking (new day: {today})")
            self.sent_today.clear()
            self.last_reset_date = today
    
    def calculate_days_from_due_date(self, due_date_str: str) -> Optional[int]:
        """
        Calculate number of days from today to due date
        Negative = days before due date
        Positive = days after due date
        Zero = today is due date
        
        Args:
            due_date_str: Due date string (ISO format: YYYY-MM-DD)
            
        Returns:
            Number of days from due date, or None if date parsing fails
        """
        try:
            # Parse due date string
            if 'T' in due_date_str:
                # Handle datetime string (YYYY-MM-DDTHH:MM:SS)
                due_date = datetime.fromisoformat(due_date_str.replace('Z', '+00:00')).date()
            else:
                # Handle date string (YYYY-MM-DD)
                due_date = datetime.strptime(due_date_str, '%Y-%m-%d').date()
            
            today = date.today()
            days_diff = (today - due_date).days
            
            logger.debug(
                f"Due date: {due_date}, Today: {today}, "
                f"Days difference: {days_diff}"
            )
            
            return days_diff
            
        except (ValueError, AttributeError) as e:
            logger.error(f"Error parsing due date '{due_date_str}': {e}")
            return None
    
    def determine_reminder_type(self, invoice: Dict) -> Optional[str]:
        """
        Determine reminder type based on due date
        
        Rules:
        - T-5 days (5 days before due date) → GENTLE
        - Due date (on the due date) → DUE
        - T+7 (7 days after due date) → FIRM
        - T+15 (15 days after due date) → ESCALATION
        
        Args:
            invoice: Invoice dictionary with 'dueDate' field
            
        Returns:
            Reminder type (GENTLE, DUE, FIRM, ESCALATION) or None if cannot determine
        """
        due_date_str = invoice.get('dueDate')
        if not due_date_str:
            logger.warning(f"Invoice {invoice.get('id')} has no due date")
            return None
        
        days_diff = self.calculate_days_from_due_date(due_date_str)
        if days_diff is None:
            return None
        
        # Determine reminder type based on rules
        if days_diff == -5:
            # Exactly 5 days before due date
            reminder_type = ReminderType.GENTLE
        elif days_diff == 0:
            # Due date
            reminder_type = ReminderType.DUE
        elif days_diff == 7:
            # 7 days after due date
            reminder_type = ReminderType.FIRM
        elif days_diff == 15:
            # 15 days after due date
            reminder_type = ReminderType.ESCALATION
        else:
            # No reminder needed for other days
            logger.debug(
                f"Invoice {invoice.get('id')}: days_diff={days_diff}, "
                f"no reminder scheduled for this day"
            )
            return None
        
        logger.info(
            f"Invoice {invoice.get('id')}: days_diff={days_diff}, "
            f"reminder_type={reminder_type}"
        )
        
        return reminder_type
    
    def should_send_reminder(
        self,
        invoice_id: int,
        reminder_type: str,
        channel: str
    ) -> bool:
        """
        Check if reminder should be sent (not already sent today)
        
        Args:
            invoice_id: Invoice ID
            reminder_type: Type of reminder
            channel: Channel (EMAIL, WHATSAPP)
            
        Returns:
            True if reminder should be sent, False if already sent today
        """
        self._reset_daily_tracking()
        
        reminder_key = (reminder_type, channel)
        
        if reminder_key in self.sent_today[invoice_id]:
            logger.info(
                f"Reminder {reminder_type} via {channel} for invoice {invoice_id} "
                f"already sent today, skipping"
            )
            return False
        
        return True
    
    def mark_reminder_sent(
        self,
        invoice_id: int,
        reminder_type: str,
        channel: str
    ):
        """
        Mark reminder as sent for today
        
        Args:
            invoice_id: Invoice ID
            reminder_type: Type of reminder
            channel: Channel (EMAIL, WHATSAPP)
        """
        self._reset_daily_tracking()
        
        reminder_key = (reminder_type, channel)
        self.sent_today[invoice_id].add(reminder_key)
        
        logger.debug(
            f"Marked reminder {reminder_type} via {channel} "
            f"for invoice {invoice_id} as sent today"
        )
    
    def get_all_reminder_types_for_invoice(self, invoice: Dict) -> list[str]:
        """
        Get all reminder types that should be sent for an invoice today
        (if multiple conditions are met)
        
        Args:
            invoice: Invoice dictionary
            
        Returns:
            List of reminder types that should be sent today
        """
        due_date_str = invoice.get('dueDate')
        if not due_date_str:
            return []
        
        days_diff = self.calculate_days_from_due_date(due_date_str)
        if days_diff is None:
            return []
        
        reminder_types = []
        
        # Check all conditions
        if days_diff == -5:
            reminder_types.append(ReminderType.GENTLE)
        if days_diff == 0:
            reminder_types.append(ReminderType.DUE)
        if days_diff == 7:
            reminder_types.append(ReminderType.FIRM)
        if days_diff == 15:
            reminder_types.append(ReminderType.ESCALATION)
        
        return reminder_types


# Convenience function for quick usage
def determine_reminder_type(invoice: Dict) -> Optional[str]:
    """
    Convenience function to determine reminder type for an invoice
    
    Args:
        invoice: Invoice dictionary with 'dueDate' field
        
    Returns:
        Reminder type or None
        
    Example:
        >>> invoice = {'id': 1, 'dueDate': '2024-01-15'}
        >>> reminder_type = determine_reminder_type(invoice)
        >>> print(reminder_type)  # 'GENTLE', 'DUE', 'FIRM', 'ESCALATION', or None
    """
    logic = ReminderLogic()
    return logic.determine_reminder_type(invoice)

