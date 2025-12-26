"""
Reminder Service
Handles reminder logic and sending reminders via different channels
"""
import logging
from typing import Dict

from src.spring_boot_client import SpringBootClient
from src.reminder_logic import ReminderLogic, ReminderType
from src.whatsapp_sender import WhatsAppSender
from src.email_sender import EmailSender
from config import config

logger = logging.getLogger(__name__)


class ReminderService:
    """Service for sending payment reminders"""
    
    def __init__(self, api_client: SpringBootClient):
        self.api_client = api_client
        self.reminder_logic = ReminderLogic()
        self.whatsapp_sender = WhatsAppSender()
        self.email_sender = EmailSender()
    
    def should_send_reminder(self, invoice: Dict) -> bool:
        """
        Determine if a reminder should be sent for this invoice
        
        Args:
            invoice: Invoice data dictionary
            
        Returns:
            True if reminder should be sent
        """
        # Check if invoice has required contact information
        customer_email = invoice.get('customerEmail')
        customer_phone = invoice.get('customerPhone')
        
        if not customer_email and not customer_phone:
            logger.warning(
                f"Invoice {invoice.get('id')} has no customer email or phone, skipping"
            )
            return False
        
        return True
    
    def send_whatsapp_reminder(self, invoice: Dict, reminder_type: str) -> bool:
        """
        Send WhatsApp reminder using mock sender
        
        Args:
            invoice: Invoice data dictionary
            reminder_type: Type of reminder
            
        Returns:
            True if sent successfully
        """
        customer_phone = invoice.get('customerPhone')
        if not customer_phone:
            logger.warning(f"Cannot send WhatsApp: no phone for invoice {invoice.get('id')}")
            return False
        
        # Generate WhatsApp message
        message = self._generate_whatsapp_message(invoice, reminder_type)
        
        # Prepare invoice data for sender
        invoice_data = {
            'invoiceNumber': invoice.get('invoiceNumber'),
            'customerName': invoice.get('customerName'),
            'reminderType': reminder_type,
            'dueDate': invoice.get('dueDate'),
            'amount': invoice.get('amount')
        }
        
        # Send WhatsApp message using mock sender
        return self.whatsapp_sender.send_message(
            phone_number=customer_phone,
            message=message,
            invoice_data=invoice_data
        )
    
    def send_email_reminder(self, invoice: Dict, reminder_type: str) -> bool:
        """
        Send email reminder using mock sender
        
        Args:
            invoice: Invoice data dictionary
            reminder_type: Type of reminder
            
        Returns:
            True if sent successfully
        """
        customer_email = invoice.get('customerEmail')
        if not customer_email:
            logger.warning(f"Cannot send email: no email for invoice {invoice.get('id')}")
            return False
        
        # Generate email subject and body
        subject = self._generate_email_subject(invoice, reminder_type)
        body = self._generate_email_body(invoice, reminder_type)
        
        # Prepare invoice data for sender
        invoice_data = {
            'invoiceNumber': invoice.get('invoiceNumber'),
            'customerName': invoice.get('customerName'),
            'reminderType': reminder_type,
            'dueDate': invoice.get('dueDate'),
            'amount': invoice.get('amount')
        }
        
        # Send email using mock sender
        return self.email_sender.send_email(
            to_email=customer_email,
            subject=subject,
            body=body,
            invoice_data=invoice_data
        )
    
    def process_reminders(self) -> Dict[str, int]:
        """
        Process all pending invoices and send reminders
        
        Returns:
            Dictionary with statistics (sent, failed, skipped)
        """
        stats = {
            'total': 0,
            'sent': 0,
            'failed': 0,
            'skipped': 0
        }
        
        try:
            # Fetch pending invoices from Java API
            logger.info("Fetching pending invoices from Spring Boot API...")
            pending_invoices = self.api_client.get_pending_invoices_for_reminder()
            stats['total'] = len(pending_invoices)
            
            logger.info(f"Found {stats['total']} pending invoices")
            
            if stats['total'] == 0:
                logger.info("No pending invoices to process")
                return stats
            
            for invoice in pending_invoices:
                try:
                    invoice_id = invoice.get('id')
                    invoice_number = invoice.get('invoiceNumber')
                    
                    logger.debug(f"Processing invoice {invoice_number} (ID: {invoice_id})")
                    
                    # Check if reminder should be sent
                    if not self.should_send_reminder(invoice):
                        stats['skipped'] += 1
                        continue
                    
                    # Determine reminder type based on due date
                    reminder_type = self.reminder_logic.determine_reminder_type(invoice)
                    
                    if not reminder_type:
                        # No reminder needed for this day
                        logger.debug(
                            f"Invoice {invoice_number}: No reminder scheduled for today"
                        )
                        stats['skipped'] += 1
                        continue
                    
                    # Determine channel (prefer email, fallback to WhatsApp)
                    channel = None
                    sent = False
                    
                    # Try email first
                    if invoice.get('customerEmail') and config.enable_email_reminders:
                        if not self.reminder_logic.should_send_reminder(
                            invoice_id, reminder_type, 'EMAIL'
                        ):
                            logger.info(
                                f"Invoice {invoice_number}: Reminder {reminder_type} via EMAIL "
                                f"already sent today, trying WhatsApp..."
                            )
                        else:
                            sent = self.send_email_reminder(invoice, reminder_type)
                            channel = 'EMAIL'
                    
                    # Try WhatsApp if email not sent or not available
                    if not sent and invoice.get('customerPhone') and config.enable_whatsapp_reminders:
                        if not self.reminder_logic.should_send_reminder(
                            invoice_id, reminder_type, 'WHATSAPP'
                        ):
                            logger.info(
                                f"Invoice {invoice_number}: Reminder {reminder_type} via WHATSAPP "
                                f"already sent today, skipping"
                            )
                            stats['skipped'] += 1
                            continue
                        
                        sent = self.send_whatsapp_reminder(invoice, reminder_type)
                        channel = 'WHATSAPP'
                    
                    if not sent:
                        logger.warning(
                            f"Invoice {invoice_number}: No contact method available or reminders disabled"
                        )
                        stats['skipped'] += 1
                        continue
                    
                    if sent and channel:
                        # Log the reminder in Java backend
                        try:
                            self.api_client.log_reminder(
                                invoice_id=invoice_id,
                                reminder_type=reminder_type,
                                channel=channel
                            )
                            
                            # Mark as sent in local tracking
                            self.reminder_logic.mark_reminder_sent(
                                invoice_id, reminder_type, channel
                            )
                            
                            stats['sent'] += 1
                            logger.info(
                                f"✓ Successfully processed reminder for invoice {invoice_number}"
                            )
                        except Exception as e:
                            logger.error(
                                f"Error logging reminder for invoice {invoice_number}: {e}"
                            )
                            stats['failed'] += 1
                    else:
                        stats['skipped'] += 1
                        
                except Exception as e:
                    logger.error(
                        f"Error processing reminder for invoice {invoice.get('id')}: {e}",
                        exc_info=True
                    )
                    stats['failed'] += 1
            
            logger.info(
                f"Reminder processing complete: {stats['sent']} sent, "
                f"{stats['failed']} failed, {stats['skipped']} skipped"
            )
            
        except Exception as e:
            logger.error(f"Error processing reminders: {e}", exc_info=True)
            raise
        
        return stats
    
    def _generate_whatsapp_message(self, invoice: Dict, reminder_type: str) -> str:
        """
        Generate WhatsApp message for reminder
        
        Args:
            invoice: Invoice dictionary
            reminder_type: Type of reminder
            
        Returns:
            Formatted WhatsApp message
        """
        invoice_number = invoice.get('invoiceNumber', 'N/A')
        customer_name = invoice.get('customerName', 'Customer')
        due_date = invoice.get('dueDate', 'N/A')
        amount = invoice.get('amount', 0)
        
        # Format amount
        amount_str = f"${amount:,.2f}" if isinstance(amount, (int, float)) else str(amount)
        
        # Generate message based on reminder type
        if reminder_type == ReminderType.GENTLE:
            message = (
                f"Hello {customer_name},\n\n"
                f"👋 Gentle reminder: Your invoice {invoice_number} "
                f"is due in 5 days (Due: {due_date}).\n\n"
                f"Amount: {amount_str}\n\n"
                f"Please ensure payment is made on time.\n\n"
                f"Thank you!"
            )
        elif reminder_type == ReminderType.DUE:
            message = (
                f"Hello {customer_name},\n\n"
                f"⏰ Payment Due Today: Your invoice {invoice_number} "
                f"is due today ({due_date}).\n\n"
                f"Amount: {amount_str}\n\n"
                f"Please make payment today to avoid any issues.\n\n"
                f"Thank you!"
            )
        elif reminder_type == ReminderType.FIRM:
            message = (
                f"Hello {customer_name},\n\n"
                f"⚠️ Payment Overdue: Your invoice {invoice_number} "
                f"is now 7 days overdue (Due: {due_date}).\n\n"
                f"Amount: {amount_str}\n\n"
                f"Please make immediate payment to avoid further action.\n\n"
                f"Thank you!"
            )
        elif reminder_type == ReminderType.ESCALATION:
            message = (
                f"Hello {customer_name},\n\n"
                f"🚨 URGENT: Your invoice {invoice_number} "
                f"is now 15 days overdue (Due: {due_date}).\n\n"
                f"Amount: {amount_str}\n\n"
                f"This is a final notice. Please contact us immediately "
                f"to resolve this matter.\n\n"
                f"Thank you!"
            )
        else:
            message = (
                f"Hello {customer_name},\n\n"
                f"Payment reminder for invoice {invoice_number}.\n\n"
                f"Amount: {amount_str}\n"
                f"Due Date: {due_date}\n\n"
                f"Thank you!"
            )
        
        return message
    
    def _generate_email_subject(self, invoice: Dict, reminder_type: str) -> str:
        """
        Generate email subject for reminder
        
        Args:
            invoice: Invoice dictionary
            reminder_type: Type of reminder
            
        Returns:
            Email subject line
        """
        invoice_number = invoice.get('invoiceNumber', 'N/A')
        
        if reminder_type == ReminderType.GENTLE:
            return f"Gentle Reminder: Payment Due Soon - Invoice {invoice_number}"
        elif reminder_type == ReminderType.DUE:
            return f"Payment Due Today - Invoice {invoice_number}"
        elif reminder_type == ReminderType.FIRM:
            return f"Payment Overdue - Invoice {invoice_number}"
        elif reminder_type == ReminderType.ESCALATION:
            return f"URGENT: Payment Overdue - Invoice {invoice_number}"
        else:
            return f"Payment Reminder - Invoice {invoice_number}"
    
    def _generate_email_body(self, invoice: Dict, reminder_type: str) -> str:
        """
        Generate email body for reminder
        
        Args:
            invoice: Invoice dictionary
            reminder_type: Type of reminder
            
        Returns:
            Formatted email body
        """
        invoice_number = invoice.get('invoiceNumber', 'N/A')
        customer_name = invoice.get('customerName', 'Customer')
        due_date = invoice.get('dueDate', 'N/A')
        amount = invoice.get('amount', 0)
        
        # Format amount
        amount_str = f"${amount:,.2f}" if isinstance(amount, (int, float)) else str(amount)
        
        # Generate email body based on reminder type
        if reminder_type == ReminderType.GENTLE:
            body = (
                f"Dear {customer_name},\n\n"
                f"This is a gentle reminder that your invoice {invoice_number} "
                f"is due in 5 days.\n\n"
                f"Invoice Details:\n"
                f"  Invoice Number: {invoice_number}\n"
                f"  Due Date: {due_date}\n"
                f"  Amount: {amount_str}\n\n"
                f"Please ensure payment is made on time to avoid any issues.\n\n"
                f"If you have already made the payment, please disregard this message.\n\n"
                f"Thank you for your business!\n\n"
                f"Best regards,\n"
                f"Payment Recovery System"
            )
        elif reminder_type == ReminderType.DUE:
            body = (
                f"Dear {customer_name},\n\n"
                f"This is to remind you that your invoice {invoice_number} "
                f"is due today.\n\n"
                f"Invoice Details:\n"
                f"  Invoice Number: {invoice_number}\n"
                f"  Due Date: {due_date}\n"
                f"  Amount: {amount_str}\n\n"
                f"Please make payment today to avoid any late fees or service interruptions.\n\n"
                f"If you have already made the payment, please disregard this message.\n\n"
                f"Thank you for your prompt attention to this matter.\n\n"
                f"Best regards,\n"
                f"Payment Recovery System"
            )
        elif reminder_type == ReminderType.FIRM:
            body = (
                f"Dear {customer_name},\n\n"
                f"This is to inform you that your invoice {invoice_number} "
                f"is now 7 days overdue.\n\n"
                f"Invoice Details:\n"
                f"  Invoice Number: {invoice_number}\n"
                f"  Due Date: {due_date}\n"
                f"  Amount: {amount_str}\n\n"
                f"Please make immediate payment to avoid further action. "
                f"We appreciate your prompt attention to this matter.\n\n"
                f"If you have already made the payment, please contact us immediately.\n\n"
                f"Best regards,\n"
                f"Payment Recovery System"
            )
        elif reminder_type == ReminderType.ESCALATION:
            body = (
                f"Dear {customer_name},\n\n"
                f"URGENT: Your invoice {invoice_number} is now 15 days overdue.\n\n"
                f"Invoice Details:\n"
                f"  Invoice Number: {invoice_number}\n"
                f"  Due Date: {due_date}\n"
                f"  Amount: {amount_str}\n\n"
                f"This is a final notice. Please contact us immediately to resolve this matter. "
                f"Failure to make payment may result in further action.\n\n"
                f"If you have already made the payment, please contact us immediately with "
                f"payment confirmation.\n\n"
                f"Best regards,\n"
                f"Payment Recovery System"
            )
        else:
            body = (
                f"Dear {customer_name},\n\n"
                f"Payment reminder for invoice {invoice_number}.\n\n"
                f"Invoice Details:\n"
                f"  Invoice Number: {invoice_number}\n"
                f"  Due Date: {due_date}\n"
                f"  Amount: {amount_str}\n\n"
                f"Thank you!\n\n"
                f"Best regards,\n"
                f"Payment Recovery System"
            )
        
        return body
