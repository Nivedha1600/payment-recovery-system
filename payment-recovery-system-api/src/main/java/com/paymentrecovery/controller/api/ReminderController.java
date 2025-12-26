package com.paymentrecovery.controller.api;

import com.paymentrecovery.model.dto.request.CreateReminderLogRequest;
import com.paymentrecovery.model.dto.response.ReminderLogResponse;
import com.paymentrecovery.service.ReminderLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Reminder operations
 * Handles HTTP requests related to reminder logs
 */
@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reminder", description = "Reminder log management APIs")
public class ReminderController {

    private final ReminderLogService reminderLogService;

    /**
     * Log a reminder that was sent
     * Creates a reminder log entry in the database
     *
     * @param request CreateReminderLogRequest containing reminder log data
     * @return ReminderLogResponse with created reminder log data
     */
    @PostMapping("/log")
    @Operation(
            summary = "Log a reminder",
            description = "Creates a reminder log entry to track that a reminder was sent. " +
                         "Records the invoice ID, reminder type, channel, and sent date."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Reminder log created successfully",
                    content = @Content(schema = @Schema(implementation = ReminderLogResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<ReminderLogResponse> logReminder(
            @Valid @RequestBody CreateReminderLogRequest request) {
        log.info("Received request to log reminder for invoice ID: {}, type: {}, channel: {}", 
                request.getInvoiceId(), request.getReminderType(), request.getChannel());

        try {
            ReminderLogResponse response = reminderLogService.createReminderLog(request);
            
            log.info("Successfully logged reminder with ID: {} for invoice ID: {}", 
                    response.getId(), request.getInvoiceId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error logging reminder for invoice ID: {}", request.getInvoiceId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

