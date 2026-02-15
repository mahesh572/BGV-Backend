package com.org.bgv.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "document_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_type_id")
    private Long docTypeId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CheckCategory category;
    
    @Column(name = "label")
    private String label; // Aadhaar Card
    
    @Column(name = "isRequired")
    private boolean isRequired;
    
    @Column(name = "upload_type")
    private String upload; // SINGLE / MULTIPLE (optional)
    
    private String code;  // AADHAAR, PAN, PASSPORT
    
    private Double price;
    
    private Boolean active = true;
    
    @Column(name = "max_files")
    private Integer maxFiles;

    // getters and setters
    
    @OneToMany(
    	    mappedBy = "documentType",
    	    cascade = CascadeType.ALL,
    	    orphanRemoval = true,
    	    fetch = FetchType.LAZY
    	)
    	@Builder.Default
    	private List<DocumentTypeAttribute> attributes = new ArrayList<>();
}
