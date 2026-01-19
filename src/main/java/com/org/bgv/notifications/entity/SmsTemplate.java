package com.org.bgv.notifications.entity;

import com.org.bgv.notifications.PolicySource;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Entity
@Table(
    name = "sms_template",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"template_code", "employer_id"}
    )
)
public class SmsTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String templateCode;

    private Long companyId;

    @Lob
    private String message;

    @Enumerated(EnumType.STRING)
    private PolicySource source;

    private boolean active;
}

