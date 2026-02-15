package com.org.bgv.notifications;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "notification_log")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;

    @Enumerated(EnumType.STRING)
    private NotificationEvent event;

    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    private String recipient;

    private String templateCode;

    private boolean success;

    private String failureReason;

    private Instant sentAt;
}

