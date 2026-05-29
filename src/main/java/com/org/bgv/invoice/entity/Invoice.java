package com.org.bgv.invoice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.enums.InvoiceStatus;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(unique = true, nullable = false)
 private String invoiceNumber;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "case_id", nullable = false)
 private VerificationCase verificationCase;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "company_id", nullable = false)
 private Company company;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "candidate_id", nullable = false)
 private Candidate candidate;

 private LocalDateTime invoiceDate;
 private LocalDateTime dueDate;
 
 @Column(precision = 10, scale = 2)
 private BigDecimal baseTotal;
 
 @Column(precision = 10, scale = 2)
 private BigDecimal addonTotal;
 
 @Column(precision = 10, scale = 2)
 private BigDecimal taxAmount;
 
 @Column(precision = 10, scale = 2)
 private BigDecimal grandTotal;
 
 private String currency;
 
 @Enumerated(EnumType.STRING)
 private InvoiceStatus status;
 
 private LocalDateTime paidAt;
 private String paymentReference;
 
 @Column(length = 500)
 private String notes;
}