package com.org.bgv.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.common.DocumentEntityType;
import com.org.bgv.common.DocumentStatus;
import com.org.bgv.constants.CaseStatus;

import jakarta.persistence.Column;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document implements BaseDocument {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_id")
    private Long docId;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CheckCategory category;

    @ManyToOne
    @JoinColumn(name = "doc_type_id")
    private DocumentType docTypeId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_check_id")
    private VerificationCaseCheck verificationCaseCheck;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private VerificationCase verificationCase;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "file_url")
    private String fileUrl;
    
    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

	/*
	 * @Column(name = "status") private String status = "active";
	 */
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DocumentStatus status;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

       
    @Column(name = "uploaded_by")
    private String uploadedBy;
    
    @Column(name = "verified")
    private boolean verified = false;
    
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
    
    @Column(name = "verified_by")
    private String verifiedBy;
    
    @Column(name = "verification_notes", length = 1000)
    private String verificationNotes;

    @Column(name = "comments")
    private String comments;

    @Column(name = "aws_doc_key")
    private String awsDocKey;

    @Column(name = "object_id")
    private Long objectId;
	
    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;
    
    @Column(name = "company_id")
    private Long companyId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    private DocumentEntityType entityType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
	
}