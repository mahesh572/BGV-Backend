package com.org.bgv.notifications.entity;

import java.util.ArrayList;
import java.util.List;

import com.org.bgv.notifications.RecipientType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "notification_policy_recipient")
@Data
public class NotificationPolicyRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private NotificationPolicy policy;

    @Enumerated(EnumType.STRING)
    private RecipientType recipient;

    @OneToMany(
        mappedBy = "recipient",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<NotificationPolicyChannel> channels = new ArrayList();
}
