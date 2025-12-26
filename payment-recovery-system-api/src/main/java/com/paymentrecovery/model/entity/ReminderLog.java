package com.paymentrecovery.model.entity;

import com.paymentrecovery.model.enums.ReminderChannel;
import com.paymentrecovery.model.enums.ReminderType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * ReminderLog entity - Tracks reminder communications sent to customers
 * Logs all reminders sent via various channels
 */
@Entity
@Table(name = "reminder_logs", indexes = {
    @Index(name = "idx_reminder_log_invoice_id", columnList = "invoice_id"),
    @Index(name = "idx_reminder_log_sent_date", columnList = "sent_date"),
    @Index(name = "idx_reminder_log_type_channel", columnList = "reminder_type" + ", " + "channel")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReminderLog extends BaseEntity {

    @Column(name = "invoice_id", nullable = false)
    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_type", nullable = false, length = 20)
    @NotNull(message = "Reminder type is required")
    private ReminderType reminderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    @NotNull(message = "Channel is required")
    private ReminderChannel channel;

    @Column(name = "sent_date", nullable = false)
    @NotNull(message = "Sent date is required")
    private LocalDateTime sentDate;
}

