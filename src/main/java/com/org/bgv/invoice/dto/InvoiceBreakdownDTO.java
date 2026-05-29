package com.org.bgv.invoice.dto;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class InvoiceBreakdownDTO {
 private Long caseId;
 private String caseReferenceNumber;
 private String candidateName;
 private String companyName;
 private Map<String, CategoryInvoiceBreakdown> breakdown;
 private BigDecimal subtotal;
 private BigDecimal tax;
 private BigDecimal grandTotal;
}




