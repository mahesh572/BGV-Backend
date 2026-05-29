package com.org.bgv.invoice.dto;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private Long caseId;
    private String caseReferenceNumber;
    private Long companyId;
    private String companyName;
    private Long candidateId;
    private String candidateName;
    private LocalDateTime invoiceDate;
    private LocalDateTime dueDate;
    private BigDecimal baseTotal;
    private BigDecimal addonTotal;
    private BigDecimal taxAmount;
    private BigDecimal grandTotal;
    private String currency;
    private String status;
    private LocalDateTime paidAt;
    private String paymentReference;
    private String notes;
    private List<InvoiceItemDTO> items;
}
