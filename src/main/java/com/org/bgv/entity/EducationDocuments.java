package com.org.bgv.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "education_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationDocuments implements BaseDocument{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doc_id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private DocumentCategory category;

    @ManyToOne
    @JoinColumn(name = "doc_type_id")
    private DocumentType type_id;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    private String file_url;
    private Long file_size;
    private String status;
    private LocalDateTime uploadedAt;
    private LocalDateTime verifiedAt;
    private String comments;
    private String awsDocKey;
    @Column(name = "object_id")
    private Long objectId; // Changed from object_id to objectId
}
