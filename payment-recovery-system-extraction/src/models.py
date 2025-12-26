"""
Pydantic models for request/response
"""
from pydantic import BaseModel, Field
from typing import Optional
from datetime import date


class ExtractInvoiceRequest(BaseModel):
    """Request model for invoice extraction"""
    invoiceId: int = Field(..., description="Invoice ID")
    filePath: str = Field(..., description="Relative file path from upload directory")


class ExtractedInvoiceData(BaseModel):
    """Extracted invoice data model"""
    invoiceNumber: Optional[str] = Field(None, description="Invoice number")
    invoiceDate: Optional[date] = Field(None, description="Invoice date")
    dueDate: Optional[date] = Field(None, description="Due date")
    amount: Optional[float] = Field(None, description="Invoice amount")
    customerName: Optional[str] = Field(None, description="Customer name")
    customerEmail: Optional[str] = Field(None, description="Customer email")
    customerPhone: Optional[str] = Field(None, description="Customer phone")
    lineItems: Optional[list] = Field(None, description="Invoice line items")
    taxAmount: Optional[float] = Field(None, description="Tax amount")
    totalAmount: Optional[float] = Field(None, description="Total amount")
    currency: Optional[str] = Field(None, description="Currency code")
    notes: Optional[str] = Field(None, description="Additional notes")


class ExtractionResponse(BaseModel):
    """Response model for extraction"""
    success: bool = Field(..., description="Whether extraction was successful")
    invoiceId: int = Field(..., description="Invoice ID")
    extractedData: Optional[ExtractedInvoiceData] = Field(None, description="Extracted data")
    message: Optional[str] = Field(None, description="Status message")
    error: Optional[str] = Field(None, description="Error message if failed")

