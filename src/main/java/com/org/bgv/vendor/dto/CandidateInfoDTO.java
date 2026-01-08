package com.org.bgv.vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateInfoDTO {
    private String name;
    private String email;
    private String phone;
    private String candidateId;
    private String candidateRef;
}
