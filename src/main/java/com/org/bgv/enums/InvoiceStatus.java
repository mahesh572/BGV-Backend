package com.org.bgv.enums;

public enum InvoiceStatus {
    PENDING,    // Awaiting payment
    PAID,       // Payment received
    OVERDUE,    // Past due date
    CANCELLED,  // Invoice cancelled
    REFUNDED,    // Refund issued
    DRAFT,
    GENERATED,
    APPROVED,
    PARTIALLY_PAID
}
