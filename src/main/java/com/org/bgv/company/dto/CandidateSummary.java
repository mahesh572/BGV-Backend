package com.org.bgv.company.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CandidateSummary {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Long candidateId;
}
