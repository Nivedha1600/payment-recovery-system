"""
FastAPI application for Invoice Extraction Service
"""
import logging
import sys
from pathlib import Path

# Add src to path
sys.path.insert(0, str(Path(__file__).parent))

from fastapi import FastAPI, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager

from src.models import ExtractInvoiceRequest, ExtractionResponse, ExtractedInvoiceData
from src.extraction_service import ExtractionService
from config import config

# Setup logging
logging.basicConfig(
    level=getattr(logging, config.log_level.upper()),
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)

logger = logging.getLogger(__name__)

# Initialize extraction service
extraction_service = ExtractionService()


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Application lifespan events"""
    # Startup
    logger.info(f"Starting {config.app_name} v{config.app_version}")
    logger.info(f"Environment: {config.environment}")
    logger.info(f"Java API URL: {config.java_api_base_url}")
    yield
    # Shutdown
    logger.info(f"Shutting down {config.app_name}")


# Create FastAPI app
app = FastAPI(
    title="Invoice Extraction Service",
    description="Microservice for extracting data from invoice files",
    version=config.app_version,
    lifespan=lifespan
)

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Configure appropriately for production
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/health")
async def health_check():
    """Health check endpoint"""
    return {"status": "healthy", "service": config.app_name}


@app.post("/api/extract-invoice", response_model=ExtractionResponse)
async def extract_invoice(request: ExtractInvoiceRequest):
    """
    Extract invoice data from file
    
    Args:
        request: ExtractInvoiceRequest with invoiceId and filePath
        
    Returns:
        ExtractionResponse with extracted data
    """
    logger.info(
        f"Received extraction request for invoice ID: {request.invoiceId}, "
        f"file: {request.filePath}"
    )
    
    try:
        # Extract invoice data
        extracted_data_dict = extraction_service.extract_invoice_data(request.filePath)
        
        # Convert to ExtractedInvoiceData model
        extracted_data = ExtractedInvoiceData(**extracted_data_dict)
        
        logger.info(
            f"Successfully extracted data for invoice ID: {request.invoiceId}. "
            f"Found invoice number: {extracted_data.invoiceNumber}"
        )
        
        return ExtractionResponse(
            success=True,
            invoiceId=request.invoiceId,
            extractedData=extracted_data,
            message="Invoice data extracted successfully"
        )
        
    except FileNotFoundError as e:
        logger.error(f"File not found: {request.filePath}", exc_info=True)
        return ExtractionResponse(
            success=False,
            invoiceId=request.invoiceId,
            error=f"File not found: {str(e)}"
        )
    except ValueError as e:
        logger.error(f"Invalid file type: {request.filePath}", exc_info=True)
        return ExtractionResponse(
            success=False,
            invoiceId=request.invoiceId,
            error=f"Unsupported file type: {str(e)}"
        )
    except Exception as e:
        logger.error(
            f"Error extracting invoice data for ID: {request.invoiceId}",
            exc_info=True
        )
        return ExtractionResponse(
            success=False,
            invoiceId=request.invoiceId,
            error=f"Extraction failed: {str(e)}"
        )


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8000,
        reload=True if config.environment == "development" else False
    )

