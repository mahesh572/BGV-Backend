package com.org.bgv.vendor.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationOutcomeDTO {

    private String code;
    private String label;
    private String description;

    private boolean success;
  //  private boolean canComplete;

    // Next status after selecting this outcome
  //  private String nextStatus;

    // Whether evidence upload becomes mandatory
    private boolean evidenceRequired;

    // Whether remarks are mandatory
    private boolean remarksRequired;
}