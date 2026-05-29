package com.org.bgv.invoice.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvoiceItemDetail {
 private String description;
 private BigDecimal price;
}
