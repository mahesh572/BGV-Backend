package com.org.bgv.invoice.dto;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class InvoiceSummaryDTO {
    private Long companyId;
    private String companyName;
    private Integer totalInvoices;
    private Integer paidInvoices;
    private Integer pendingInvoices;
    private Integer overdueInvoices;
    private Integer cancelledInvoices;
    private BigDecimal totalRevenue;
    private BigDecimal pendingAmount;
    private BigDecimal overdueAmount;
    private Map<String, BigDecimal> revenueByMonth; // Month-Year -> Amount
}
