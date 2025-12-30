package com.org.bgv.vendor.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeclaredCriminalInfoDTO {
    private Boolean hasCriminalRecord;
    private String courtDetails;
    private String caseNumber;
    private LocalDateTime caseDate;
    private String offenseType;
    private String status; // acquitted, convicted, pending
    private String location;
    private LocalDateTime declaredOn;
    private Boolean selfDeclared;
    private String additionalInfo;
}