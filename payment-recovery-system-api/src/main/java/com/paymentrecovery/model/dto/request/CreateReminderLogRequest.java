package com.paymentrecovery.model.dto.request;

import com.paymentrecovery.model.enums.ReminderChannel;
import com.paymentrecovery.model.enums.ReminderType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a reminder log
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReminderLogRequest {

    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;

    @NotNull(message = "Reminder type is required")
    private ReminderType reminderType;

    @NotNull(message = "Channel is required")
    private ReminderChannel channel;
}

