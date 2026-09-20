package com.org.bgv.policy.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.org.bgv.policy.enums.VersionStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "policy_versions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @Column(nullable = false)
    private String version;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String contentType; // HTML, MARKDOWN

    private String documentUrl;

    private String changeSummary;

    @Column(nullable = false)
    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;

    @Column(nullable = false)
    private Boolean currentVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VersionStatus status;

    private LocalDateTime publishedAt;
}
