package com.paymentrecovery.model.dto.response;

import com.paymentrecovery.model.enums.ReminderChannel;
import com.paymentrecovery.model.enums.ReminderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for reminder log
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderLogResponse {

    private Long id;
    private Long invoiceId;
    private ReminderType reminderType;
    private ReminderChannel channel;
    private LocalDateTime sentDate;
    private LocalDateTime createdAt;
}

