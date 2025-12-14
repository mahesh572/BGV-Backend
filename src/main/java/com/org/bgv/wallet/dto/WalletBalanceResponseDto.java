package com.org.bgv.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalanceResponseDto {
    private Long walletId;
    private Long userId;
    private BigDecimal balance;
    private String currency;
    private Boolean isActive;
}
