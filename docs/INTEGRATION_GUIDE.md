# Payment Recovery System - Integration Guide

## Java-Python Integration Architecture

### Overview
Python automation services communicate with Java backend exclusively through REST API. Python services never access the database directly.

## API Endpoints for Python Services

### Base URL
```
Production: https://api.paymentrecovery.com/api/v1/automation
Staging:    https://api-staging.paymentrecovery.com/api/v1/automation
Development: http://localhost:8080/api/v1/automation
```

### Authentication

#### Service-to-Service Authentication
Python services authenticate using API keys or OAuth2 client credentials.

**Header Format:**
```
Authorization: Bearer {api_key}
X-Service-Name: python-scheduler
```

**Java Configuration:**
```java
@Configuration
public class AutomationSecurityConfig {
    
    @Bean
    public SecurityFilterChain automationApiFilterChain(HttpSecurity http) {
        return http
            .securityMatcher("/api/v1/automation/**")
            .authorizeHttpRequests(auth -> auth
                .anyRequest().hasRole("AUTOMATION_SERVICE"))
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtDecoder(jwtDecoder())))
            .build();
    }
}
```

### API Endpoints

#### 1. Scheduling API

##### Get Scheduled Tasks
```http
GET /api/v1/automation/tasks/scheduled
Authorization: Bearer {api_key}
```

**Response:**
```json
{
  "tasks": [
    {
      "id": "task-123",
      "type": "REMINDER",
      "scheduledAt": "2024-01-15T10:00:00Z",
      "recoveryCaseId": "case-456",
      "status": "PENDING",
      "metadata": {
        "reminderType": "EMAIL",
        "templateId": "reminder-1"
      }
    }
  ],
  "total": 1,
  "page": 0,
  "size": 20
}
```

##### Create Scheduled Task
```http
POST /api/v1/automation/tasks
Authorization: Bearer {api_key}
Content-Type: application/json

{
  "type": "REMINDER",
  "scheduledAt": "2024-01-15T10:00:00Z",
  "recoveryCaseId": "case-456",
  "metadata": {
    "reminderType": "EMAIL",
    "templateId": "reminder-1"
  }
}
```

##### Update Task Status
```http
PUT /api/v1/automation/tasks/{taskId}/status
Authorization: Bearer {api_key}
Content-Type: application/json

{
  "status": "COMPLETED",
  "result": {
    "sentAt": "2024-01-15T10:00:05Z",
    "recipient": "customer@example.com"
  }
}
```

#### 2. Recovery Cases API

##### Get Pending Recovery Cases
```http
GET /api/v1/automation/recovery-cases/pending
Authorization: Bearer {api_key}
Query Parameters:
  - status: PENDING|OVERDUE|IN_PROGRESS
  - priority: HIGH|MEDIUM|LOW
  - limit: number (default: 100)
```

**Response:**
```json
{
  "cases": [
    {
      "id": "case-456",
      "customerId": "cust-789",
      "amount": 5000.00,
      "currency": "USD",
      "dueDate": "2024-01-10",
      "status": "OVERDUE",
      "priority": "HIGH",
      "lastReminderSentAt": "2024-01-12T09:00:00Z",
      "customer": {
        "id": "cust-789",
        "name": "Acme Corp",
        "email": "billing@acme.com",
        "phone": "+1-555-0123"
      }
    }
  ],
  "total": 1
}
```

##### Update Recovery Case
```http
PUT /api/v1/automation/recovery-cases/{caseId}
Authorization: Bearer {api_key}
Content-Type: application/json

{
  "status": "IN_PROGRESS",
  "lastReminderSentAt": "2024-01-15T10:00:00Z",
  "notes": "Reminder sent via email"
}
```

#### 3. Reminders API

##### Create Reminder Record
```http
POST /api/v1/automation/reminders
Authorization: Bearer {api_key}
Content-Type: application/json

{
  "recoveryCaseId": "case-456",
  "type": "EMAIL",
  "templateId": "reminder-1",
  "scheduledFor": "2024-01-15T10:00:00Z",
  "recipient": "billing@acme.com",
  "metadata": {
    "subject": "Payment Reminder",
    "personalization": {
      "customerName": "Acme Corp",
      "amount": 5000.00,
      "dueDate": "2024-01-10"
    }
  }
}
```

##### Record Reminder Sent
```http
PUT /api/v1/automation/reminders/{reminderId}/sent
Authorization: Bearer {api_key}
Content-Type: application/json

{
  "sentAt": "2024-01-15T10:00:05Z",
  "channel": "EMAIL",
  "externalId": "email-msg-123",
  "status": "SENT"
}
```

#### 4. Extraction API

##### Submit Extracted Data
```http
POST /api/v1/automation/extractions
Authorization: Bearer {api_key}
Content-Type: application/json

{
  "documentId": "doc-123",
  "extractionType": "INVOICE",
  "extractedData": {
    "invoiceNumber": "INV-2024-001",
    "amount": 5000.00,
    "dueDate": "2024-01-10",
    "customerName": "Acme Corp",
    "lineItems": [
      {
        "description": "Service Fee",
        "amount": 5000.00
      }
    ]
  },
  "confidence": 0.95,
  "extractedAt": "2024-01-15T10:00:00Z"
}
```

##### Get Documents for Extraction
```http
GET /api/v1/automation/documents/pending-extraction
Authorization: Bearer {api_key}
Query Parameters:
  - type: INVOICE|PAYMENT|STATEMENT
  - limit: number (default: 50)
```

**Response:**
```json
{
  "documents": [
    {
      "id": "doc-123",
      "type": "INVOICE",
      "fileName": "invoice-2024-001.pdf",
      "uploadedAt": "2024-01-15T09:00:00Z",
      "downloadUrl": "https://storage.example.com/documents/doc-123",
      "metadata": {
        "recoveryCaseId": "case-456",
        "mimeType": "application/pdf"
      }
    }
  ],
  "total": 1
}
```

#### 5. Notifications API

##### Create Notification
```http
POST /api/v1/automation/notifications
Authorization: Bearer {api_key}
Content-Type: application/json

{
  "recoveryCaseId": "case-456",
  "type": "EMAIL",
  "recipient": "billing@acme.com",
  "subject": "Payment Reminder",
  "body": "Your payment of $5000.00 is overdue...",
  "priority": "HIGH",
  "scheduledFor": "2024-01-15T10:00:00Z"
}
```

##### Update Notification Status
```http
PUT /api/v1/automation/notifications/{notificationId}/status
Authorization: Bearer {api_key}
Content-Type: application/json

{
  "status": "SENT",
  "sentAt": "2024-01-15T10:00:05Z",
  "externalId": "email-msg-123",
  "error": null
}
```

## Python Service Implementation

### Python Client Library

```python
# automation_client.py
import requests
from typing import Optional, Dict, List, Any
from datetime import datetime
import logging

logger = logging.getLogger(__name__)

class AutomationApiClient:
    """Client for Java Automation API"""
    
    def __init__(self, base_url: str, api_key: str):
        self.base_url = base_url.rstrip('/')
        self.api_key = api_key
        self.session = requests.Session()
        self.session.headers.update({
            'Authorization': f'Bearer {api_key}',
            'Content-Type': 'application/json',
            'X-Service-Name': 'python-automation'
        })
    
    def _request(self, method: str, endpoint: str, **kwargs) -> Dict[str, Any]:
        """Make API request with error handling"""
        url = f"{self.base_url}{endpoint}"
        
        try:
            response = self.session.request(method, url, **kwargs)
            response.raise_for_status()
            return response.json() if response.content else {}
        except requests.exceptions.RequestException as e:
            logger.error(f"API request failed: {method} {url} - {e}")
            raise
    
    def get_pending_recovery_cases(
        self, 
        status: Optional[str] = None,
        priority: Optional[str] = None,
        limit: int = 100
    ) -> List[Dict[str, Any]]:
        """Get pending recovery cases"""
        params = {'limit': limit}
        if status:
            params['status'] = status
        if priority:
            params['priority'] = priority
        
        response = self._request('GET', '/recovery-cases/pending', params=params)
        return response.get('cases', [])
    
    def update_recovery_case(
        self, 
        case_id: str, 
        updates: Dict[str, Any]
    ) -> Dict[str, Any]:
        """Update recovery case"""
        return self._request('PUT', f'/recovery-cases/{case_id}', json=updates)
    
    def get_scheduled_tasks(self) -> List[Dict[str, Any]]:
        """Get scheduled tasks"""
        response = self._request('GET', '/tasks/scheduled')
        return response.get('tasks', [])
    
    def create_scheduled_task(self, task_data: Dict[str, Any]) -> Dict[str, Any]:
        """Create scheduled task"""
        return self._request('POST', '/tasks', json=task_data)
    
    def update_task_status(
        self, 
        task_id: str, 
        status: str, 
        result: Optional[Dict[str, Any]] = None
    ) -> Dict[str, Any]:
        """Update task status"""
        payload = {'status': status}
        if result:
            payload['result'] = result
        return self._request('PUT', f'/tasks/{task_id}/status', json=payload)
    
    def get_pending_documents(
        self, 
        doc_type: Optional[str] = None,
        limit: int = 50
    ) -> List[Dict[str, Any]]:
        """Get documents pending extraction"""
        params = {'limit': limit}
        if doc_type:
            params['type'] = doc_type
        
        response = self._request('GET', '/documents/pending-extraction', params=params)
        return response.get('documents', [])
    
    def submit_extraction(
        self, 
        document_id: str, 
        extraction_data: Dict[str, Any]
    ) -> Dict[str, Any]:
        """Submit extracted data"""
        payload = {
            'documentId': document_id,
            **extraction_data
        }
        return self._request('POST', '/extractions', json=payload)
    
    def create_reminder(self, reminder_data: Dict[str, Any]) -> Dict[str, Any]:
        """Create reminder record"""
        return self._request('POST', '/reminders', json=reminder_data)
    
    def record_reminder_sent(
        self, 
        reminder_id: str, 
        sent_data: Dict[str, Any]
    ) -> Dict[str, Any]:
        """Record reminder as sent"""
        return self._request('PUT', f'/reminders/{reminder_id}/sent', json=sent_data)
    
    def create_notification(self, notification_data: Dict[str, Any]) -> Dict[str, Any]:
        """Create notification"""
        return self._request('POST', '/notifications', json=notification_data)
    
    def update_notification_status(
        self, 
        notification_id: str, 
        status: str, 
        sent_at: Optional[datetime] = None,
        external_id: Optional[str] = None,
        error: Optional[str] = None
    ) -> Dict[str, Any]:
        """Update notification status"""
        payload = {'status': status}
        if sent_at:
            payload['sentAt'] = sent_at.isoformat()
        if external_id:
            payload['externalId'] = external_id
        if error:
            payload['error'] = error
        
        return self._request(
            'PUT', 
            f'/notifications/{notification_id}/status', 
            json=payload
        )
```

### Example Python Service

```python
# reminder_service.py
from automation_client import AutomationApiClient
from datetime import datetime, timedelta
import logging

logger = logging.getLogger(__name__)

class ReminderService:
    """Service for sending payment reminders"""
    
    def __init__(self, api_client: AutomationApiClient):
        self.api_client = api_client
    
    def process_pending_reminders(self):
        """Process all pending reminders"""
        # Get scheduled tasks
        tasks = self.api_client.get_scheduled_tasks()
        
        for task in tasks:
            if task['type'] == 'REMINDER' and task['status'] == 'PENDING':
                try:
                    self._send_reminder(task)
                    self.api_client.update_task_status(
                        task['id'], 
                        'COMPLETED',
                        {'sentAt': datetime.utcnow().isoformat()}
                    )
                except Exception as e:
                    logger.error(f"Failed to send reminder {task['id']}: {e}")
                    self.api_client.update_task_status(
                        task['id'], 
                        'FAILED',
                        {'error': str(e)}
                    )
    
    def _send_reminder(self, task: Dict[str, Any]):
        """Send a reminder for a task"""
        # Get recovery case details
        case_id = task['recoveryCaseId']
        case = self.api_client.get_pending_recovery_cases()[0]  # Simplified
        
        # Create reminder record
        reminder = self.api_client.create_reminder({
            'recoveryCaseId': case_id,
            'type': task['metadata']['reminderType'],
            'templateId': task['metadata']['templateId'],
            'scheduledFor': task['scheduledAt'],
            'recipient': case['customer']['email'],
            'metadata': {
                'subject': 'Payment Reminder',
                'personalization': {
                    'customerName': case['customer']['name'],
                    'amount': case['amount'],
                    'dueDate': case['dueDate']
                }
            }
        })
        
        # Send email (using email service)
        # ... email sending logic ...
        
        # Record as sent
        self.api_client.record_reminder_sent(reminder['id'], {
            'sentAt': datetime.utcnow().isoformat(),
            'channel': 'EMAIL',
            'externalId': 'email-msg-123',
            'status': 'SENT'
        })
        
        # Update case
        self.api_client.update_recovery_case(case_id, {
            'lastReminderSentAt': datetime.utcnow().isoformat(),
            'notes': 'Reminder sent via email'
        })
```

## Error Handling & Retry Logic

### Retry Strategy

```python
# retry_utils.py
import time
from functools import wraps
from typing import Callable, TypeVar, Tuple
import logging

logger = logging.getLogger(__name__)

T = TypeVar('T')

def retry_with_backoff(
    max_retries: int = 3,
    initial_delay: float = 1.0,
    backoff_factor: float = 2.0,
    exceptions: Tuple[Exception, ...] = (Exception,)
):
    """Retry decorator with exponential backoff"""
    def decorator(func: Callable[..., T]) -> Callable[..., T]:
        @wraps(func)
        def wrapper(*args, **kwargs) -> T:
            delay = initial_delay
            last_exception = None
            
            for attempt in range(max_retries):
                try:
                    return func(*args, **kwargs)
                except exceptions as e:
                    last_exception = e
                    if attempt < max_retries - 1:
                        logger.warning(
                            f"Attempt {attempt + 1} failed: {e}. "
                            f"Retrying in {delay}s..."
                        )
                        time.sleep(delay)
                        delay *= backoff_factor
                    else:
                        logger.error(f"All {max_retries} attempts failed")
            
            raise last_exception
        return wrapper
    return decorator

# Usage
@retry_with_backoff(max_retries=3, initial_delay=1.0)
def api_call():
    return api_client.get_pending_recovery_cases()
```

## Configuration

### Environment Variables

```python
# config.py
import os
from dataclasses import dataclass

@dataclass
class Config:
    java_api_base_url: str = os.getenv('JAVA_API_BASE_URL', 'http://localhost:8080')
    java_api_key: str = os.getenv('JAVA_API_KEY', '')
    max_retries: int = int(os.getenv('MAX_RETRIES', '3'))
    retry_delay: float = float(os.getenv('RETRY_DELAY', '1.0'))
    log_level: str = os.getenv('LOG_LEVEL', 'INFO')
```

## Testing

### Mock API for Testing

```python
# test_automation_client.py
import pytest
from unittest.mock import Mock, patch
from automation_client import AutomationApiClient

def test_get_pending_cases():
    client = AutomationApiClient('http://test-api', 'test-key')
    
    with patch('requests.Session.request') as mock_request:
        mock_response = Mock()
        mock_response.json.return_value = {
            'cases': [{'id': 'case-1', 'status': 'PENDING'}]
        }
        mock_response.content = b'{}'
        mock_request.return_value = mock_response
        
        cases = client.get_pending_recovery_cases()
        assert len(cases) == 1
        assert cases[0]['id'] == 'case-1'
```

## Best Practices

1. **Idempotency:** Ensure operations can be safely retried
2. **Rate Limiting:** Respect rate limits from Java API
3. **Logging:** Log all API calls for debugging
4. **Error Handling:** Handle all error cases gracefully
5. **Timeouts:** Set appropriate timeouts for API calls
6. **Connection Pooling:** Reuse HTTP connections
7. **Circuit Breaker:** Implement circuit breaker for resilience

