package com.org.bgv.notifications;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Entity
@Table(
    name = "in_app_template",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"template_code", "company_id"}
    )
)
public class InAppTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String templateCode;
    
    private String template_name;

    private Long companyId;

    private String title;

    private String message;
    
    @Column(name = "type", length = 50)
    private String type; // e.g., "account_creation", "password_reset"

    private String deepLink;
    
    private NotificationPriority priority;

    @Enumerated(EnumType.STRING)
    private PolicySource source;

    private boolean active;

    


}
