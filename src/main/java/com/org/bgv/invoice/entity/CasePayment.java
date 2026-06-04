package com.org.bgv.invoice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.org.bgv.constants.PaymentStatus;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.WalletTransaction;
import com.org.bgv.enums.PaymentMethod;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="case_payment")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CasePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="case_id")
    private VerificationCase verificationCase;

    @ManyToOne
    @JoinColumn(name="invoice_id")
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name="wallet_transaction_id")
    private WalletTransaction walletTransaction;

    private BigDecimal amount;

    private LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // WALLET / RAZORPAY

    private String paymentReference;
    
    private String razorpayOrderId;

    private String razorpayPaymentId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    

    private String remarks;
}