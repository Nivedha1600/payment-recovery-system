# Python Service Integration

## Overview

The Java backend integrates with Python extraction service using REST API calls. The integration is **fire-and-forget** (async, non-blocking) to avoid blocking user requests.

## Architecture

```
User Uploads File
    ↓
Java Backend
    ├── Save file to storage
    ├── Create DRAFT invoice
    └── Trigger async extraction (fire-and-forget)
         ↓
    Python Service (async)
         ├── Extract invoice data
         └── Call Java API to update invoice
```

## Components

### 1. InvoiceExtractionService
- **Location**: `service/InvoiceExtractionService.java`
- **Purpose**: Calls Python service for invoice data extraction
- **Method**: `triggerExtraction(invoiceId, filePath)`
- **Execution**: Async (fire-and-forget)

### 2. AsyncConfig
- **Location**: `config/AsyncConfig.java`
- **Purpose**: Configures async execution thread pool
- **Thread Pool**: 5-10 threads, queue capacity 100

### 3. RestTemplateConfig
- **Location**: `config/RestTemplateConfig.java`
- **Purpose**: Configures RestTemplate for HTTP calls
- **Timeout**: 5s connect, 30s read

### 4. ExtractInvoiceDataRequest
- **Location**: `model/dto/request/ExtractInvoiceDataRequest.java`
- **Fields**: `invoiceId`, `filePath`

## API Call

### Request to Python Service

**Endpoint**: `POST {pythonServiceUrl}/api/extract-invoice`

**URL**: Configured via `app.python.extraction.url` (default: `http://localhost:8000`)

**Payload**:
```json
{
  "invoiceId": 1,
  "filePath": "1/2024/01/15/uuid.pdf"
}
```

**Headers**:
- `Content-Type: application/json`

## Configuration

### application.properties

```properties
# Python Extraction Service Configuration
app.python.extraction.url=http://localhost:8000
app.python.extraction.endpoint=/api/extract-invoice
app.python.extraction.timeout=30000
```

### Environment Variables

- `PYTHON_SERVICE_URL` - Python service base URL

## Integration Flow

1. **User uploads invoice file** → `POST /api/invoices/upload`
2. **Java backend**:
   - Validates file type
   - Stores file to local storage
   - Creates DRAFT invoice in database
   - Returns invoice ID immediately
3. **Async extraction** (fire-and-forget):
   - Calls Python service with `invoiceId` and `filePath`
   - Does not wait for response
   - Does not block user request

## Error Handling

- **Fire-and-forget**: Errors are logged but don't affect the upload
- **Retry**: Can be implemented later if needed
- **Monitoring**: Logs all extraction triggers and errors

## Benefits

1. **Non-blocking**: User gets immediate response
2. **Scalable**: Async execution doesn't block threads
3. **Resilient**: Errors don't affect file upload
4. **Flexible**: Python service can process at its own pace

## Python Service Expected Endpoint

The Python service should implement:

**POST** `/api/extract-invoice`

**Request Body**:
```json
{
  "invoiceId": 1,
  "filePath": "1/2024/01/15/uuid.pdf"
}
```

**Python Service Responsibilities**:
1. Read file from Java storage (or Java provides file content)
2. Extract invoice data (amount, date, invoice number, etc.)
3. Call Java API to update invoice with extracted data
4. Update invoice status from DRAFT to PENDING

## Testing

### Test Async Execution

```java
@Autowired
private InvoiceExtractionService extractionService;

@Test
void testAsyncExtraction() {
    // This returns immediately (fire-and-forget)
    extractionService.triggerExtraction(1L, "1/2024/01/15/file.pdf");
    
    // Wait a bit to see async execution
    Thread.sleep(1000);
}
```

## Monitoring

Check logs for:
- `Triggering invoice data extraction for invoice ID: X`
- `Successfully triggered extraction for invoice ID: X`
- `Error triggering extraction for invoice ID: X` (if errors occur)

## Future Enhancements

1. **Retry Logic**: Add retry mechanism for failed extractions
2. **WebClient**: Migrate from RestTemplate to WebClient (reactive)
3. **Queue**: Use message queue (RabbitMQ/Kafka) for better reliability
4. **Status Tracking**: Track extraction status in database
5. **Webhooks**: Python service can call webhook when extraction completes

