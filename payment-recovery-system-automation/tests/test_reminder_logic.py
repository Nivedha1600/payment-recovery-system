"""
Unit tests for Reminder Logic
"""
import pytest
from datetime import date, timedelta
from src.reminder_logic import ReminderLogic, ReminderType, determine_reminder_type


class TestReminderLogic:
    """Test cases for ReminderLogic"""
    
    @pytest.fixture
    def logic(self):
        """Create a ReminderLogic instance"""
        return ReminderLogic()
    
    def test_calculate_days_from_due_date_future(self, logic):
        """Test calculating days when due date is in the future"""
        future_date = (date.today() + timedelta(days=5)).strftime('%Y-%m-%d')
        days_diff = logic.calculate_days_from_due_date(future_date)
        assert days_diff == -5
    
    def test_calculate_days_from_due_date_today(self, logic):
        """Test calculating days when due date is today"""
        today = date.today().strftime('%Y-%m-%d')
        days_diff = logic.calculate_days_from_due_date(today)
        assert days_diff == 0
    
    def test_calculate_days_from_due_date_past(self, logic):
        """Test calculating days when due date is in the past"""
        past_date = (date.today() - timedelta(days=7)).strftime('%Y-%m-%d')
        days_diff = logic.calculate_days_from_due_date(past_date)
        assert days_diff == 7
    
    def test_determine_reminder_type_gentle(self, logic):
        """Test determining GENTLE reminder (T-5)"""
        invoice = {
            'id': 1,
            'dueDate': (date.today() + timedelta(days=5)).strftime('%Y-%m-%d')
        }
        reminder_type = logic.determine_reminder_type(invoice)
        assert reminder_type == ReminderType.GENTLE
    
    def test_determine_reminder_type_due(self, logic):
        """Test determining DUE reminder (T+0)"""
        invoice = {
            'id': 1,
            'dueDate': date.today().strftime('%Y-%m-%d')
        }
        reminder_type = logic.determine_reminder_type(invoice)
        assert reminder_type == ReminderType.DUE
    
    def test_determine_reminder_type_firm(self, logic):
        """Test determining FIRM reminder (T+7)"""
        invoice = {
            'id': 1,
            'dueDate': (date.today() - timedelta(days=7)).strftime('%Y-%m-%d')
        }
        reminder_type = logic.determine_reminder_type(invoice)
        assert reminder_type == ReminderType.FIRM
    
    def test_determine_reminder_type_escalation(self, logic):
        """Test determining ESCALATION reminder (T+15)"""
        invoice = {
            'id': 1,
            'dueDate': (date.today() - timedelta(days=15)).strftime('%Y-%m-%d')
        }
        reminder_type = logic.determine_reminder_type(invoice)
        assert reminder_type == ReminderType.ESCALATION
    
    def test_determine_reminder_type_no_match(self, logic):
        """Test determining reminder type when no rule matches"""
        invoice = {
            'id': 1,
            'dueDate': (date.today() - timedelta(days=3)).strftime('%Y-%m-%d')
        }
        reminder_type = logic.determine_reminder_type(invoice)
        assert reminder_type is None
    
    def test_determine_reminder_type_no_due_date(self, logic):
        """Test determining reminder type when invoice has no due date"""
        invoice = {'id': 1}
        reminder_type = logic.determine_reminder_type(invoice)
        assert reminder_type is None
    
    def test_should_send_reminder_first_time(self, logic):
        """Test should_send_reminder when not sent today"""
        result = logic.should_send_reminder(1, ReminderType.GENTLE, 'EMAIL')
        assert result is True
    
    def test_should_send_reminder_already_sent(self, logic):
        """Test should_send_reminder when already sent today"""
        invoice_id = 1
        reminder_type = ReminderType.GENTLE
        channel = 'EMAIL'
        
        # Mark as sent
        logic.mark_reminder_sent(invoice_id, reminder_type, channel)
        
        # Should return False
        result = logic.should_send_reminder(invoice_id, reminder_type, channel)
        assert result is False
    
    def test_should_send_reminder_different_channel(self, logic):
        """Test should_send_reminder with different channel"""
        invoice_id = 1
        reminder_type = ReminderType.GENTLE
        
        # Mark EMAIL as sent
        logic.mark_reminder_sent(invoice_id, reminder_type, 'EMAIL')
        
        # WHATSAPP should still be allowed
        result = logic.should_send_reminder(invoice_id, reminder_type, 'WHATSAPP')
        assert result is True
    
    def test_should_send_reminder_different_type(self, logic):
        """Test should_send_reminder with different reminder type"""
        invoice_id = 1
        channel = 'EMAIL'
        
        # Mark GENTLE as sent
        logic.mark_reminder_sent(invoice_id, ReminderType.GENTLE, channel)
        
        # DUE should still be allowed
        result = logic.should_send_reminder(invoice_id, ReminderType.DUE, channel)
        assert result is True
    
    def test_get_all_reminder_types_for_invoice(self, logic):
        """Test getting all reminder types for an invoice"""
        invoice = {
            'id': 1,
            'dueDate': date.today().strftime('%Y-%m-%d')
        }
        types = logic.get_all_reminder_types_for_invoice(invoice)
        assert ReminderType.DUE in types
        assert len(types) == 1


class TestConvenienceFunction:
    """Test cases for convenience function"""
    
    def test_determine_reminder_type_function(self):
        """Test the convenience function"""
        invoice = {
            'id': 1,
            'dueDate': date.today().strftime('%Y-%m-%d')
        }
        reminder_type = determine_reminder_type(invoice)
        assert reminder_type == ReminderType.DUE

