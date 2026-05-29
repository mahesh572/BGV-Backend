package com.org.bgv.invoice.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItem {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "invoice_id", nullable = false)
 private Invoice invoice;

 @Column(name = "selection_id")
 private Long selectionId; // Reference to verification_case_selection.id

 private String category;
 private String description;
 
 @Column(precision = 10, scale = 2)
 private BigDecimal quantity;
 
 @Column(precision = 10, scale = 2)
 private BigDecimal unitPrice;
 
 @Column(precision = 10, scale = 2)
 private BigDecimal totalPrice;
}