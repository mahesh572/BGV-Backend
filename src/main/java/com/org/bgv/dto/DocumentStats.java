package com.org.bgv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentStats {
    private long totalDocuments;
    private long verified;
    private long pending;
    private long rejected;
}
