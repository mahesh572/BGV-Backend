package com.org.bgv.notifications.entity;

import java.util.ArrayList;
import java.util.List;

import com.org.bgv.notifications.NotificationEvent;
import com.org.bgv.notifications.PolicySource;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Entity
@Table(
    name = "notification_policy",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"event", "company_id"}
    )
)
public class NotificationPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationEvent event;

    // NULL = platform default
    private Long companyId;

    @Enumerated(EnumType.STRING)
    private PolicySource source;

    private boolean active;

    @OneToMany(
        mappedBy = "policy",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<NotificationPolicyChannel> channels = new ArrayList();
}

