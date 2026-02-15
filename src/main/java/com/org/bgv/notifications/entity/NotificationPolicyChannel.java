package com.org.bgv.notifications.entity;

import com.org.bgv.notifications.NotificationChannel;
import com.org.bgv.notifications.NotificationPriority;
import com.org.bgv.notifications.RecipientType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "notification_policy_channel")
public class NotificationPolicyChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private NotificationPolicy policy;

    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    private boolean enabled;

	/*
	 * @Enumerated(EnumType.STRING) private RecipientType recipient;
	 */

    // Template to use (resolved later)
    private String templateCode;

    @Enumerated(EnumType.STRING)
    private NotificationPriority priority;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_recipient_id")
    private NotificationPolicyRecipient recipient;
    
}
