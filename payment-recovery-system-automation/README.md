# Payment Recovery Automation Service

Python automation service for scheduling and sending payment reminders. This service communicates with the Java backend via REST API and handles reminder scheduling and dispatch.

## Architecture

This service is part of the Payment Recovery System architecture:
- **Java Backend**: System of record (database access)
- **Python Service**: Automation (scheduling, reminders, extraction)
- **Communication**: REST API (Python → Java)

## Features

- **Scheduled Reminder Processing**: Automatically processes pending invoices at configured intervals
- **Multi-Channel Support**: Email and WhatsApp reminders
- **Reminder Type Logic**: Automatically determines reminder type (GENTLE, DUE, FIRM, ESCALATION)
- **API Integration**: Communicates with Java backend via REST API
- **Retry Logic**: Automatic retry with exponential backoff
- **Logging**: Comprehensive logging for monitoring and debugging

## Prerequisites

- Python 3.11 or higher
- Access to Java backend API
- (Optional) Email/WhatsApp service credentials if sending directly

## Setup

### 1. Create Virtual Environment

```bash
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
```

### 2. Install Dependencies

```bash
pip install -r requirements.txt
```

### 3. Configure Environment

Copy `.env.example` to `.env` and update with your configuration:

```bash
cp .env.example .env
```

Edit `.env` with your settings:

```env
JAVA_API_BASE_URL=http://localhost:8080
JAVA_API_KEY=your-api-key-here
REMINDER_CHECK_INTERVAL=3600  # Check every hour
```

### 4. Run the Service

```bash
python main.py
```

## Configuration

### Environment Variables

Key configuration options (see `.env.example` for all options):

- `JAVA_API_BASE_URL`: Java backend API URL
- `JAVA_API_KEY`: API key for authentication
- `REMINDER_CHECK_INTERVAL`: How often to check for reminders (seconds)
- `ENABLE_EMAIL_REMINDERS`: Enable/disable email reminders
- `ENABLE_WHATSAPP_REMINDERS`: Enable/disable WhatsApp reminders
- `LOG_LEVEL`: Logging level (DEBUG, INFO, WARNING, ERROR)

### Configuration File

Configuration is managed in `config.py` which loads from:
1. Environment variables
2. `.env` file
3. Default values

## Project Structure

```
payment-recovery-system-automation/
├── src/
│   ├── __init__.py
│   ├── api_client.py          # Java API client
│   ├── reminder_service.py   # Reminder logic
│   └── scheduler.py          # Task scheduling
├── config.py                  # Configuration management
├── main.py                    # Entry point
├── requirements.txt           # Python dependencies
├── .env.example               # Environment variables template
├── .gitignore
└── README.md
```

## Usage

### Running the Service

```bash
# Development
python main.py

# Production (with process manager)
# Use systemd, supervisor, or Docker
```

### API Integration

The service communicates with Java backend:

1. **Fetch Pending Invoices**: `GET /api/invoices/pending-for-reminder`
2. **Log Reminder**: `POST /api/reminders/log`
3. **Mark Invoice Paid**: `POST /api/invoices/{id}/mark-paid`

### Reminder Processing Flow

1. Scheduler triggers reminder processing job
2. Service fetches pending invoices from Java API
3. For each invoice:
   - Determines reminder type based on due date
   - Checks if reminder should be sent
   - Sends reminder via appropriate channel (email/WhatsApp)
   - Logs reminder in Java backend

## Development

### Running Tests

```bash
pytest
```

### Code Formatting

```bash
black src/
```

### Type Checking

```bash
mypy src/
```

## Deployment

### Docker (Recommended)

```dockerfile
FROM python:3.11-slim

WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

CMD ["python", "main.py"]
```

### Systemd Service

Create `/etc/systemd/system/payment-recovery-automation.service`:

```ini
[Unit]
Description=Payment Recovery Automation Service
After=network.target

[Service]
Type=simple
User=paymentrecovery
WorkingDirectory=/opt/payment-recovery-automation
Environment="PATH=/opt/payment-recovery-automation/venv/bin"
ExecStart=/opt/payment-recovery-automation/venv/bin/python main.py
Restart=always

[Install]
WantedBy=multi-user.target
```

## Monitoring

- Check logs for reminder processing statistics
- Monitor API response times
- Track reminder success/failure rates
- Monitor scheduler job execution

## Troubleshooting

### Service won't start
- Check Java API is accessible
- Verify API key is correct
- Check configuration in `.env`

### Reminders not sending
- Verify email/WhatsApp services are configured
- Check logs for errors
- Ensure Java API is returning pending invoices

### API connection errors
- Verify `JAVA_API_BASE_URL` is correct
- Check network connectivity
- Verify API key is valid

## Security

- Never commit `.env` file
- Use secure API keys
- Rotate credentials regularly
- Use HTTPS for API communication in production

## License

[Your License Here]

