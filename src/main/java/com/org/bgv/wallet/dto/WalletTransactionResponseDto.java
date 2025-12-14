package com.org.bgv.wallet.dto;

import com.org.bgv.constants.TransactionStatus;
import com.org.bgv.constants.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionResponseDto {
    private Long transactionId;
    private String transactionRef;
    private BigDecimal amount;
    private TransactionType transactionType;
    private TransactionStatus status;
    private String description;
    private LocalDateTime createdAt;
    private BigDecimal balanceAfterTransaction;
}
