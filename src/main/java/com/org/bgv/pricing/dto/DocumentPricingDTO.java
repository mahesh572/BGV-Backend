package com.org.bgv.pricing.dto;

import java.math.BigDecimal;

import com.org.bgv.enums.PricingType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentPricingDTO {

    private Long pricingId;          // platform_document_pricing.id
    private Long categoryId;
    private String categoryName;

    private Long documentTypeId;     // document_type.doc_type_id
    private String documentName;     // name
    private String documentCode;     // AADHAR, PAN
    private String documentLabel;    // Aadhaar Card

    private PricingType pricingType; // FLAT / PER_RECORD
    private BigDecimal  unitPrice;

    private Double minCharge;        // optional
    private Double maxCharge;        // optional

    private Boolean active;
}