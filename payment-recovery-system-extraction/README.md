# Invoice Extraction Service

Python microservice for extracting data from invoice files using FastAPI.

## Features

- **REST API**: FastAPI-based RESTful service
- **Multiple File Types**: Supports PDF, Images, Excel
- **Extraction Methods**:
  - PDF: Text extraction using pdfplumber
  - Images: Mock data (OCR can be added)
  - Excel: Data extraction using pandas
- **JSON Response**: Returns extracted data as JSON

## Prerequisites

- Python 3.11+
- Access to Java backend file storage

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

Create `.env` file:

```env
JAVA_API_BASE_URL=http://localhost:8080
JAVA_API_KEY=your-api-key
JAVA_FILE_BASE_PATH=uploads/invoices
LOG_LEVEL=INFO
ENVIRONMENT=development
```

### 4. Run the Service

```bash
# Development
python main.py

# Or using uvicorn directly
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

## API Endpoints

### POST /extract-invoice

Extract invoice data from file.

**Request Body**:
```json
{
  "invoiceId": 1,
  "filePath": "1/2024/01/15/uuid.pdf"
}
```

**Response**:
```json
{
  "success": true,
  "invoiceId": 1,
  "extractedData": {
    "invoiceNumber": "INV-2024-001",
    "invoiceDate": "2024-01-15",
    "dueDate": "2024-02-15",
    "amount": 5000.00,
    "customerName": "Acme Corp",
    "totalAmount": 5000.00,
    "currency": "USD"
  },
  "message": "Invoice data extracted successfully"
}
```

## File Type Support

### PDF
- Uses `pdfplumber` for text extraction
- Extracts text and tables
- Pattern matching for invoice fields

### Images (PNG, JPG, etc.)
- Currently returns mock data
- Can be enhanced with OCR (Tesseract)

### Excel (XLS, XLSX)
- Uses `pandas` for data reading
- Extracts data from sheets
- Identifies invoice fields and line items

## Project Structure

```
payment-recovery-system-extraction/
├── src/
│   ├── __init__.py
│   ├── models.py              # Pydantic models
│   ├── file_type_detector.py  # File type detection
│   ├── extraction_service.py  # Main extraction service
│   └── extractors/
│       ├── pdf_extractor.py   # PDF extraction
│       ├── image_extractor.py # Image extraction
│       └── excel_extractor.py # Excel extraction
├── config.py                  # Configuration
├── main.py                    # FastAPI application
├── requirements.txt           # Dependencies
└── README.md
```

## Testing

### Using cURL

```bash
curl -X POST http://localhost:8000/extract-invoice \
  -H "Content-Type: application/json" \
  -d '{
    "invoiceId": 1,
    "filePath": "1/2024/01/15/invoice.pdf"
  }'
```

### Using Python

```python
import requests

response = requests.post(
    'http://localhost:8000/extract-invoice',
    json={
        'invoiceId': 1,
        'filePath': '1/2024/01/15/invoice.pdf'
    }
)

print(response.json())
```

## Integration with Java Backend

The Java backend calls this service after file upload:

1. Java uploads file and creates DRAFT invoice
2. Java calls: `POST http://localhost:8000/extract-invoice`
3. Python extracts data
4. Python returns extracted data (Java can update invoice)

## Future Enhancements

1. **OCR for Images**: Implement Tesseract OCR
2. **ML-based Extraction**: Use ML models for better accuracy
3. **DOC/DOCX Support**: Add Word document extraction
4. **Validation**: Validate extracted data
5. **Update Java API**: Call Java API to update invoice with extracted data

## License

[Your License Here]

