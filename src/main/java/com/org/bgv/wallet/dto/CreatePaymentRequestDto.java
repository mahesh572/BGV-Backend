package com.org.bgv.wallet.dto;

import com.org.bgv.constants.PaymentPurpose;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequestDto {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Company ID is required")
    private Long companyId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Amount must be at least ₹1")
    private BigDecimal amount;
    
    private String currency = "INR";
    
    @NotNull(message = "Payment purpose is required")
    private PaymentPurpose purpose;
    
    private String description;
    
    private Map<String, Object> metadata;
    
    private String callbackUrl;
    
    private String webhookUrl;
}
