package com.org.bgv.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSummaryDto {
    private Integer totalCategories;
    private Integer totalDocuments;
    private Integer approvedDocuments;
    private Integer pendingDocuments;
    private Integer rejectedDocuments;
    private Integer uploadProgress;
    private Boolean isComplete;
    private Integer companiesCount;
}