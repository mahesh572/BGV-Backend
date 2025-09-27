package com.org.bgv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentSummary {
    private int totalCompanies;
    private long totalDocuments;
    private long verifiedDocuments;
    private long pendingDocuments;
    private long rejectedDocuments;
    private int completionPercentage;
}
