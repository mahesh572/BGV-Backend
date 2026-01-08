package com.org.bgv.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayOrderResponseDto {
    private String orderId;
    private String entity;
    private Long amount;
    private Long amountPaid;
    private Long amountDue;
    private String currency;
    private String receipt;
    private String status;
    private Integer attempts;
    private String notes;
    private Long createdAt;
    private String razorpayKey;
}
