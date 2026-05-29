package com.org.bgv.invoice.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CategoryInvoiceBreakdown {
 private String categoryName;
 private List<InvoiceItemDetail> items = new ArrayList();
 private BigDecimal total = BigDecimal.ZERO;
 
 public CategoryInvoiceBreakdown(String categoryName) {
     this.categoryName = categoryName;
 }
 
 public void addItem(String description, BigDecimal price) {
     this.items.add(new InvoiceItemDetail(description, price));
 }
}