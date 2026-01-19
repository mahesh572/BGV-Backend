package com.org.bgv.notifications.entity;

import java.util.List;

import com.org.bgv.notifications.NotificationEvent;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "notification_event")
@Getter
@Setter
public class NotificationEventMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_code", unique = true, nullable = false)
    private NotificationEvent eventCode;

    @Column(nullable = false)
    private String displayName;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String severity;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "notification_event_channels",
        joinColumns = @JoinColumn(name = "event_id")
    )
    @Column(name = "channel")
    private List<String> supportedChannels;

    private Boolean isActive = true;
}


