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
public class ReferenceContextDTO {
    private String verificationStage; // contact_attempted, reference_responded
    private Integer contactAttempts;
    private LocalDateTime lastContactAttempt;
    private String preferredContactMethod;
    private String referenceResponseStatus;
    private Boolean isReferenceCooperative;
    private String availabilityTime;
}
