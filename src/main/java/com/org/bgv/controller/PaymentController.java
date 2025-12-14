package com.org.bgv.controller;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.wallet.dto.CreatePaymentRequestDto;
import com.org.bgv.wallet.dto.RazorpayOrderResponseDto;
import com.org.bgv.wallet.dto.WalletBalanceResponseDto;
import com.org.bgv.wallet.dto.WalletTransactionResponseDto;
import com.org.bgv.wallet.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    
    private final PaymentService paymentService;
    
    /**
     * Create Razorpay payment order
     */
    @PostMapping("/create-order")
    public ResponseEntity<CustomApiResponse<RazorpayOrderResponseDto>> createPaymentOrder(
            @Valid @RequestBody CreatePaymentRequestDto requestDto) {
        try {
            log.info("Creating payment order for user: {}, company: {}, amount: {}", 
                    requestDto.getUserId(), requestDto.getCompanyId(), requestDto.getAmount());
            
            RazorpayOrderResponseDto response = paymentService.createPaymentOrder(requestDto);
            
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Payment order created successfully", 
                            response, 
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Failed to create payment order: {}", e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to create payment order: " + e.getMessage(), 
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
    /**
     * Verify and process payment callback
     */
    @PostMapping("/verify")
    public ResponseEntity<CustomApiResponse<String>> verifyPayment(
            @RequestParam String razorpay_payment_id,
            @RequestParam String razorpay_order_id,
            @RequestParam String razorpay_signature) {
        
        try {
            log.info("Verifying payment: orderId={}, paymentId={}", 
                    razorpay_order_id, razorpay_payment_id);
            
            String result = paymentService.verifyAndProcessPayment(razorpay_payment_id, razorpay_order_id, razorpay_signature);
            
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            result, 
                            "Payment processed", 
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Payment verification failed: {}", e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            "Payment verification failed: " + e.getMessage(), 
                            HttpStatus.BAD_REQUEST
                    ));
        }
    }
    
    /**
     * Get wallet balance
     */
    @GetMapping("/wallet/balance")
    public ResponseEntity<CustomApiResponse<WalletBalanceResponseDto>> getWalletBalance(
            
            @RequestParam Long companyId) {
        
        try {
            log.info("Getting wallet balance for  company: {}", companyId);
            
            WalletBalanceResponseDto response = paymentService.getWalletBalance(companyId);
            
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Wallet balance retrieved successfully", 
                            response, 
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Failed to get wallet balance: {}", e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to retrieve wallet balance: " + e.getMessage(), 
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
    /**
     * Get wallet transactions with pagination
     */
    @GetMapping("/wallet/transactions")
    public ResponseEntity<CustomApiResponse<Page<WalletTransactionResponseDto>>> getWalletTransactions(
            @RequestParam Long companyId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        try {
            log.info("Getting wallet transactions for companyId: {}, page: {}, size: {}", 
            		companyId, pageable.getPageNumber(), pageable.getPageSize());
            
            Page<WalletTransactionResponseDto> response = paymentService.getWalletTransactions(companyId, pageable);
            
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Wallet transactions retrieved successfully", 
                            response, 
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Failed to get wallet transactions: {}", e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to retrieve wallet transactions: " + e.getMessage(), 
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
    /**
     * Make payment from wallet
     */
    @PostMapping("/wallet/pay")
    public ResponseEntity<CustomApiResponse<WalletTransactionResponseDto>> makePaymentFromWallet(
            @RequestParam Long userId,
            @RequestParam Long companyId,
            @RequestParam BigDecimal amount,
            @RequestParam String description) {
        
        try {
            log.info("Processing wallet payment for user: {}, company: {}, amount: {}", 
                    userId, companyId, amount);
            
            WalletTransactionResponseDto response = paymentService.makePaymentFromWallet(
                    userId, companyId, amount, description);
            
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Payment processed successfully from wallet", 
                            response, 
                            HttpStatus.OK
                    )
            );
        } catch (RuntimeException e) {
            log.error("Wallet payment failed: {}", e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            "Payment failed: " + e.getMessage(), 
                            HttpStatus.BAD_REQUEST
                    ));
        } catch (Exception e) {
            log.error("Failed to process wallet payment: {}", e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to process payment: " + e.getMessage(), 
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
    /**
     * Handle Razorpay webhook
     */
    @PostMapping("/webhook")
    public ResponseEntity<CustomApiResponse<String>> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {
        
        try {
            log.info("Received Razorpay webhook payload: {}", payload);
            
            // Verify webhook signature
            if (!verifyWebhookSignature(payload, signature)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(CustomApiResponse.failure(
                                "Invalid webhook signature", 
                                HttpStatus.UNAUTHORIZED
                        ));
            }
            
            // Process webhook events
            processWebhookEvents(payload);
            
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Webhook processed successfully", 
                            "Webhook received and processed", 
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Webhook processing failed: {}", e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Webhook processing failed: " + e.getMessage(), 
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
    /**
     * Get payment request by reference
     */
    @GetMapping("/request/{requestRef}")
    public ResponseEntity<CustomApiResponse<Object>> getPaymentRequest(
            @PathVariable String requestRef) {
        
        try {
            log.info("Getting payment request by reference: {}", requestRef);
            
            // Implement service method to get payment request
            // Object response = paymentService.getPaymentRequestByRef(requestRef);
            
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Payment request retrieved successfully", 
                            null, // Replace with actual response
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Failed to get payment request: {}", e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to retrieve payment request: " + e.getMessage(), 
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
    /**
     * Get wallet transaction summary
     */
    @GetMapping("/wallet/summary")
    public ResponseEntity<CustomApiResponse<Object>> getWalletSummary(
            @RequestParam Long userId,
            @RequestParam Long companyId) {
        
        try {
            log.info("Getting wallet summary for user: {}, company: {}", userId, companyId);
            
            // Implement service method to get wallet summary
            // Object response = paymentService.getWalletSummary(userId, companyId);
            
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Wallet summary retrieved successfully", 
                            null, // Replace with actual response
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Failed to get wallet summary: {}", e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to retrieve wallet summary: " + e.getMessage(), 
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
    /**
     * Refund payment
     */
    @PostMapping("/refund")
    public ResponseEntity<CustomApiResponse<String>> refundPayment(
            @RequestParam String paymentId,
            @RequestParam(required = false) BigDecimal amount) {
        
        try {
            log.info("Processing refund for payment: {}, amount: {}", paymentId, amount);
            
            // Implement service method to process refund
            // paymentService.processRefund(paymentId, amount);
            
            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Refund initiated successfully", 
                            "Refund processed", 
                            HttpStatus.OK
                    )
            );
        } catch (Exception e) {
            log.error("Refund failed: {}", e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Refund failed: " + e.getMessage(), 
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
    
    /**
     * Verify webhook signature (implement actual verification)
     */
    private boolean verifyWebhookSignature(String payload, String signature) {
        // Implement actual Razorpay webhook signature verification
        // For example: Utils.verifyWebhookSignature(payload, signature, secret);
        return true; // Placeholder - implement proper verification
    }
    
    /**
     * Process webhook events
     */
    private void processWebhookEvents(String payload) {
        // Parse and process different webhook events:
        // - payment.captured
        // - payment.failed
        // - refund.created
        // - order.paid
        // etc.
        
        log.info("Processing webhook events: {}", payload);
        // Implement event processing logic
    }
}
