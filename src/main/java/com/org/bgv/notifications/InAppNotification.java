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

@Entity
@Table(name = "in_app_notification")
public class InAppNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recipientUserId;

    @Enumerated(EnumType.STRING)
    private RecipientRole role;

    @Enumerated(EnumType.STRING)
    private NotificationEvent event;

    private String eventId;   // idempotency

    private String entityId;  // caseId (BGV)

    private String title;
    private String message;
    private String deepLink;

    @Enumerated(EnumType.STRING)
    private NotificationPriority priority;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;  // UNREAD, READ

    private Instant createdAt;
    private Instant readAt;

    @Lob
    private String metadata;  // JSON
}
