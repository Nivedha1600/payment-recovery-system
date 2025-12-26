"""
Scheduler Service
Handles scheduled tasks for reminder automation
Runs daily at 9 AM to process payment reminders
"""
import logging
from datetime import datetime, time
from typing import Optional

from apscheduler.schedulers.blocking import BlockingScheduler
from apscheduler.triggers.cron import CronTrigger
from apscheduler.events import EVENT_JOB_EXECUTED, EVENT_JOB_ERROR

from src.reminder_service import ReminderService
from src.api_client import AutomationApiClient
from src.spring_boot_client import SpringBootClient
from config import config

logger = logging.getLogger(__name__)


class ReminderScheduler:
    """Scheduler for reminder automation tasks"""
    
    def __init__(self, reminder_service: ReminderService):
        self.reminder_service = reminder_service
        self.scheduler = BlockingScheduler(timezone=config.scheduler_timezone)
        self._setup_event_listeners()
    
    def _setup_event_listeners(self):
        """Setup scheduler event listeners"""
        def job_executed_listener(event):
            logger.info(f"Job {event.job_id} executed successfully at {datetime.now()}")
        
        def job_error_listener(event):
            logger.error(
                f"Job {event.job_id} failed with exception: {event.exception}",
                exc_info=event.exception
            )
        
        self.scheduler.add_listener(job_executed_listener, EVENT_JOB_EXECUTED)
        self.scheduler.add_listener(job_error_listener, EVENT_JOB_ERROR)
    
    def schedule_daily_reminder_processing(self, hour: int = 9, minute: int = 0):
        """
        Schedule daily reminder processing job at specified time
        
        Args:
            hour: Hour of day (0-23), default 9 (9 AM)
            minute: Minute of hour (0-59), default 0
        """
        # Schedule job to run daily at specified time
        trigger = CronTrigger(hour=hour, minute=minute)
        
        self.scheduler.add_job(
            func=self._process_reminders_job,
            trigger=trigger,
            id='daily_reminder_processing',
            name='Daily Payment Reminder Processing',
            replace_existing=True,
            max_instances=1,  # Prevent concurrent execution
            coalesce=True,    # Combine multiple pending executions
            misfire_grace_time=300  # Allow 5 minutes grace period
        )
        
        logger.info(
            f"Scheduled daily reminder processing job to run at {hour:02d}:{minute:02d}"
        )
    
    def _process_reminders_job(self):
        """Job function to process reminders"""
        try:
            logger.info("=" * 60)
            logger.info(f"Starting daily reminder processing at {datetime.now()}")
            logger.info("=" * 60)
            
            # Process reminders
            stats = self.reminder_service.process_reminders()
            
            logger.info("=" * 60)
            logger.info("Reminder processing completed:")
            logger.info(f"  Total invoices: {stats['total']}")
            logger.info(f"  Reminders sent: {stats['sent']}")
            logger.info(f"  Failed: {stats['failed']}")
            logger.info(f"  Skipped: {stats['skipped']}")
            logger.info("=" * 60)
            
        except Exception as e:
            logger.error(f"Error in reminder processing job: {e}", exc_info=True)
            raise
    
    def start(self):
        """Start the scheduler"""
        logger.info("Starting reminder scheduler")
        self.schedule_daily_reminder_processing()
        logger.info("Scheduler started. Waiting for scheduled jobs...")
        self.scheduler.start()
    
    def shutdown(self):
        """Shutdown the scheduler gracefully"""
        logger.info("Shutting down reminder scheduler")
        self.scheduler.shutdown(wait=True)


def create_scheduler() -> ReminderScheduler:
    """Factory function to create scheduler with dependencies"""
    # Use SpringBootClient for API calls
    api_client = SpringBootClient(
        base_url=config.java_api_base_url,
        api_key=config.java_api_key,
        timeout=config.java_api_timeout
    )
    
    # Create reminder service with API client
    reminder_service = ReminderService(api_client)
    
    return ReminderScheduler(reminder_service)
