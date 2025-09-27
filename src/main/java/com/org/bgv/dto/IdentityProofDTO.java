package com.org.bgv.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class IdentityProofDTO {
    private Long id;
   
    private String documentNumber;
    private String status;
    private LocalDateTime uploadedAt;
    private String updatedBy;

    private List<DocumentResponse> documents;
   // private DocumentStats documentStats;
}
