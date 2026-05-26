package com.org.bgv.invoice.dto;


import com.org.bgv.enums.InvoiceStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InvoiceFilterDTO {
    private InvoiceStatus status;
    private Long companyId;
    private Long candidateId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
