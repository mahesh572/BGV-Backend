package com.org.bgv.invoice.dto;


import lombok.Data;

@Data
public class InvoicePaymentRequest {
    private String paymentReference;
    private String paymentMethod; // CARD, BANK_TRANSFER, CASH, CHEQUE, UPI
    private String notes;
}
