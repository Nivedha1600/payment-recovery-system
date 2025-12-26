"""
Main entry point for Payment Recovery Automation Service
"""
import logging
import signal
import sys
from pathlib import Path

# Add src to path
sys.path.insert(0, str(Path(__file__).parent))

from src.scheduler import create_scheduler
from config import config


def setup_logging():
    """Setup logging configuration"""
    log_format = '%(asctime)s - %(name)s - %(levelname)s - %(message)s'
    
    if config.log_format == 'json':
        # Use structured logging for JSON format
        import structlog
        structlog.configure(
            processors=[
                structlog.processors.TimeStamper(fmt="iso"),
                structlog.processors.JSONRenderer()
            ],
            wrapper_class=structlog.make_filtering_bound_logger(
                logging.getLevelName(config.log_level)
            ),
            context_class=dict,
            logger_factory=structlog.PrintLoggerFactory(),
            cache_logger_on_first_use=False,
        )
    else:
        # Use standard logging
        logging.basicConfig(
            level=getattr(logging, config.log_level.upper()),
            format=log_format,
            handlers=[
                logging.StreamHandler(sys.stdout)
            ]
        )


def main():
    """Main function"""
    # Setup logging
    setup_logging()
    logger = logging.getLogger(__name__)
    
    # Validate configuration
    try:
        config.validate()
    except ValueError as e:
        logger.error(f"Configuration error: {e}")
        sys.exit(1)
    
    logger.info(f"Starting {config.app_name} v{config.app_version}")
    logger.info(f"Environment: {config.environment}")
    logger.info(f"Java API URL: {config.java_api_base_url}")
    
    # Create and start scheduler
    scheduler = create_scheduler()
    
    # Setup signal handlers for graceful shutdown
    def signal_handler(sig, frame):
        logger.info("Received shutdown signal, shutting down gracefully...")
        scheduler.shutdown()
        sys.exit(0)
    
    signal.signal(signal.SIGINT, signal_handler)
    signal.signal(signal.SIGTERM, signal_handler)
    
    try:
        # Start scheduler (this will block)
        scheduler.start()
    except KeyboardInterrupt:
        logger.info("Received keyboard interrupt, shutting down...")
        scheduler.shutdown()
    except Exception as e:
        logger.error(f"Fatal error: {e}", exc_info=True)
        scheduler.shutdown()
        sys.exit(1)


if __name__ == '__main__':
    main()

