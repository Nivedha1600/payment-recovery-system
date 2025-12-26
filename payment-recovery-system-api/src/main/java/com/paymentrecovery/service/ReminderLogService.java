package com.paymentrecovery.service;

import com.paymentrecovery.model.dto.request.CreateReminderLogRequest;
import com.paymentrecovery.model.dto.response.ReminderLogResponse;
import com.paymentrecovery.model.entity.ReminderLog;
import com.paymentrecovery.repository.ReminderLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for ReminderLog business logic
 * Handles reminder log operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderLogService {

    private final ReminderLogRepository reminderLogRepository;

    /**
     * Create a new reminder log entry
     *
     * @param request CreateReminderLogRequest containing reminder log data
     * @return ReminderLogResponse with created reminder log data
     */
    @Transactional
    public ReminderLogResponse createReminderLog(CreateReminderLogRequest request) {
        log.debug("Creating reminder log for invoice ID: {}, type: {}, channel: {}", 
                request.getInvoiceId(), request.getReminderType(), request.getChannel());

        ReminderLog reminderLog = new ReminderLog();
        reminderLog.setInvoiceId(request.getInvoiceId());
        reminderLog.setReminderType(request.getReminderType());
        reminderLog.setChannel(request.getChannel());
        reminderLog.setSentDate(LocalDateTime.now());

        ReminderLog savedReminderLog = reminderLogRepository.save(reminderLog);

        log.info("Successfully created reminder log with ID: {} for invoice ID: {}", 
                savedReminderLog.getId(), request.getInvoiceId());

        return mapToResponse(savedReminderLog);
    }

    /**
     * Maps ReminderLog entity to ReminderLogResponse DTO
     *
     * @param reminderLog ReminderLog entity
     * @return ReminderLogResponse DTO
     */
    private ReminderLogResponse mapToResponse(ReminderLog reminderLog) {
        return ReminderLogResponse.builder()
                .id(reminderLog.getId())
                .invoiceId(reminderLog.getInvoiceId())
                .reminderType(reminderLog.getReminderType())
                .channel(reminderLog.getChannel())
                .sentDate(reminderLog.getSentDate())
                .createdAt(reminderLog.getCreatedAt())
                .build();
    }
}

