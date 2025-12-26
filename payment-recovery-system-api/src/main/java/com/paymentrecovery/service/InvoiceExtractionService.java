package com.paymentrecovery.service;

import com.paymentrecovery.model.dto.request.ExtractInvoiceDataRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service for calling Python extraction service
 * Handles async communication with Python service for invoice data extraction
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceExtractionService {

    @Value("${app.python.extraction.url:http://localhost:8000}")
    private String pythonServiceUrl;

    @Value("${app.python.extraction.endpoint:/api/extract-invoice}")
    private String extractionEndpoint;

    @Value("${app.python.extraction.timeout:30000}")
    private int timeout;

    private final RestTemplate restTemplate;

    /**
     * Trigger invoice data extraction in Python service
     * This is a fire-and-forget operation (async, non-blocking)
     * The method executes asynchronously and does not block the caller
     *
     * @param invoiceId Invoice ID
     * @param filePath File path of the uploaded invoice
     */
    @Async("taskExecutor")
    public void triggerExtraction(Long invoiceId, String filePath) {
        log.info("Triggering invoice data extraction for invoice ID: {}, file: {}", invoiceId, filePath);

        try {
            ExtractInvoiceDataRequest request = new ExtractInvoiceDataRequest(invoiceId, filePath);
            String url = pythonServiceUrl + extractionEndpoint;

            log.debug("Calling Python extraction service: {}", url);

            // Fire-and-forget: Make async call without waiting for response
            restTemplate.postForObject(url, request, String.class);

            log.info("Successfully triggered extraction for invoice ID: {}", invoiceId);

        } catch (Exception e) {
            // Log error but don't throw - this is fire-and-forget
            log.error(
                    "Error triggering extraction for invoice ID: {}, file: {}",
                    invoiceId, filePath, e
            );
        }
    }
}

