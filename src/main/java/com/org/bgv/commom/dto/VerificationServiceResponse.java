package com.org.bgv.commom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationServiceResponse {

    private Long categoryId;

    private String categoryCode;

    private String categoryName;

    private String serviceName;

   // private Boolean hasDocuments;

  //  private Double price;
}