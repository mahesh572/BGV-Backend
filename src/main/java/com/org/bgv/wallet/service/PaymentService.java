package com.org.bgv.wallet.service;


import com.org.bgv.config.SecurityUtils;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.constants.PaymentPurpose;
import com.org.bgv.constants.PaymentStatus;
import com.org.bgv.constants.TransactionStatus;
import com.org.bgv.constants.TransactionType;
import com.org.bgv.entity.*;
import com.org.bgv.enums.InvoiceStatus;
import com.org.bgv.enums.PaymentMethod;
import com.org.bgv.invoice.entity.CasePayment;
import com.org.bgv.invoice.entity.Invoice;
import com.org.bgv.invoice.repository.CasePaymentRepository;
import com.org.bgv.invoice.repository.InvoiceRepository;
import com.org.bgv.repository.*;
import com.org.bgv.service.VendorAssignmentService;
import com.org.bgv.wallet.dto.CreatePaymentRequestDto;
import com.org.bgv.wallet.dto.RazorpayOrderResponseDto;
import com.org.bgv.wallet.dto.WalletBalanceResponseDto;
import com.org.bgv.wallet.dto.WalletTransactionResponseDto;
import com.org.bgv.wallet.repository.PaymentRequestRepository;
import com.org.bgv.wallet.repository.WalletRepository;
import com.org.bgv.wallet.repository.WalletTransactionRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final PaymentRequestRepository paymentRequestRepository;
    private final RazorpayClient razorpayClient;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CasePaymentRepository casePaymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final VerificationCaseRepository verificationCaseRepository;
    private final VendorAssignmentService vendorAssignmentService;
    private final VerificationCaseCheckRepository verificationCaseCheckRepository;
    
    
    @Value("${razorpay.key.id}")
    private String razorpayKeyId;
    
    @Value("${razorpay.callback.url}")
    private String defaultCallbackUrl;
    
    @Value("${razorpay.webhook.url}")
    private String webhookUrl;
    
    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;
    
    
    
    /**
     * Create Razorpay order for payment
     */
    @Transactional
    public RazorpayOrderResponseDto createPaymentOrder(CreatePaymentRequestDto requestDto) {

        try {

            String requestRef =
                    "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            PaymentRequest paymentRequest = PaymentRequest.builder()
                    .requestRef(requestRef)
                    .userId(requestDto.getUserId())
                    .companyId(requestDto.getCompanyId())
                    .amount(requestDto.getAmount())
                    .currency(requestDto.getCurrency())
                    .purpose(requestDto.getPurpose())
                    .description(requestDto.getDescription())

                    // NEW FIELDS
                    .caseId(requestDto.getCaseId())
                    .invoiceId(requestDto.getInvoiceId())
                    .invoiceNumber(requestDto.getInvoiceNumber())
                    .paymentMethod(requestDto.getPaymentMethod())
                    .walletAmountUsed(
                            requestDto.getWalletAmountUsed() == null
                                    ? BigDecimal.ZERO
                                    : requestDto.getWalletAmountUsed())
                    .onlineAmount(
                            requestDto.getOnlineAmount() == null
                                    ? requestDto.getAmount()
                                    : requestDto.getOnlineAmount())

                    .callbackUrl(
                            requestDto.getCallbackUrl() != null
                                    ? requestDto.getCallbackUrl()
                                    : defaultCallbackUrl)

                    .webhookUrl(
                            requestDto.getWebhookUrl() != null
                                    ? requestDto.getWebhookUrl()
                                    : webhookUrl)

                    .expiresAt(LocalDateTime.now().plusHours(24))
                    .build();
            
            log.info("Before Save PaymentRequest:");
            log.info("paymentMethod={}", paymentRequest.getPaymentMethod());

            paymentRequest = paymentRequestRepository.save(paymentRequest);

            // For wallet only payment no Razorpay order required
            if (PaymentMethod.WALLET.equals(requestDto.getPaymentMethod())) {

                return RazorpayOrderResponseDto.builder()
                        .orderId(null)
                        .amount(0L)
                        .currency("INR")
                        .status("WALLET_PAYMENT")
                        .razorpayKey(null)
                        .build();
            }

            BigDecimal onlineAmount =
                    requestDto.getOnlineAmount() == null
                            ? requestDto.getAmount()
                            : requestDto.getOnlineAmount();

            JSONObject orderRequest = new JSONObject();

            orderRequest.put(
                    "amount",
                    onlineAmount.multiply(BigDecimal.valueOf(100)).longValue());

            orderRequest.put("currency", requestDto.getCurrency());
            orderRequest.put("receipt", requestRef);
            orderRequest.put("payment_capture", 1);

            JSONObject notes = new JSONObject();

            notes.put("companyId", requestDto.getCompanyId());

            if (requestDto.getCaseId() != null) {
                notes.put("caseId", requestDto.getCaseId());
            }

            if (requestDto.getInvoiceNumber() != null) {
                notes.put("invoiceNumber", requestDto.getInvoiceNumber());
            }

            notes.put("paymentType", requestDto.getPaymentMethod());

            orderRequest.put("notes", notes);

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            paymentRequest.setRazorpayOrderId(
                    razorpayOrder.get("id").toString());

            paymentRequest.setGatewayResponse(
                    razorpayOrder.toString());

            paymentRequest.setStatus(PaymentStatus.CREATED);

            paymentRequestRepository.save(paymentRequest);

            return RazorpayOrderResponseDto.builder()
                    .orderId(razorpayOrder.get("id"))
                    .entity(razorpayOrder.get("entity"))
                    .amount(((Number) razorpayOrder.get("amount")).longValue())
                    .amountDue(((Number) razorpayOrder.get("amount_due")).longValue())
                    .amountPaid(((Number) razorpayOrder.get("amount_paid")).longValue())
                    .currency(razorpayOrder.get("currency"))
                    .receipt(razorpayOrder.get("receipt"))
                    .status(razorpayOrder.get("status"))
                    .attempts(razorpayOrder.get("attempts"))
                    .notes(razorpayOrder.get("notes").toString())
                    .createdAt(getEpochSeconds(razorpayOrder.get("created_at")))
                    .razorpayKey(razorpayKeyId)
                    .build();

        } catch (Exception e) {

            log.error("Payment order creation failed", e);

            throw new RuntimeException(
                    "Failed to create payment order : " + e.getMessage());
        }
    }
    
    /**
     * Verify Razorpay payment and update wallet
     */
    @Transactional
    public String verifyAndProcessPayment(String razorpayPaymentId, String razorpayOrderId, String razorpaySignature) {
        try {
            log.info("=== Starting payment verification ===");
            log.info("Payment ID: {}, Order ID: {}", razorpayPaymentId, razorpayOrderId);
            
            // Check if transaction already exists
            Optional<WalletTransaction> existingTransaction = 
                walletTransactionRepository.findByRazorpayPaymentId(razorpayPaymentId);
            
            if (existingTransaction.isPresent()) {
                WalletTransaction existing = existingTransaction.get();
                log.warn("Transaction already exists for payment ID: {}. Status: {}", 
                        razorpayPaymentId, existing.getStatus());
                
                if (existing.getStatus() == TransactionStatus.FAILED) {
                    log.info("Found failed transaction, attempting to retry...");
                    // Continue processing for retry
                } else {
                    log.info("Transaction already processed successfully. Skipping...");
                    return "Payment has already been processed with status: " + existing.getStatus();
                }
            }
            
            // Verify payment signature
            String generatedSignature = generateSignature(razorpayOrderId + "|" + razorpayPaymentId);
            
            if (!generatedSignature.equals(razorpaySignature)) {
                // Create failed transaction record
                createFailedTransaction(razorpayPaymentId, razorpayOrderId, razorpaySignature,
                    "Invalid payment signature");
                throw new RuntimeException("Invalid payment signature");
            }
            
            // Find payment request
            PaymentRequest paymentRequest = paymentRequestRepository.findByRazorpayOrderId(razorpayOrderId)
                    .orElseThrow(() -> {
                        createFailedTransaction(razorpayPaymentId, razorpayOrderId, razorpaySignature,
                            "Payment request not found");
                        return new RuntimeException("Payment request not found");
                    });
            
            // Check if already processed
            if (paymentRequest.getStatus() == PaymentStatus.PAID) {
                log.warn("Payment already processed for order ID: {}", razorpayOrderId);
                
                // Check if transaction exists but wasn't found earlier (race condition)
                Boolean existingTransactionExisted = walletTransactionRepository.existsByRazorpayOrderId(razorpayOrderId);
                if (existingTransactionExisted) {
                    log.info("Transaction found for order ID: {}", razorpayOrderId);
                }
                return "Payment request already marked as PAID";
            }
            
            log.info("Creating new transaction for payment...");
            
            try {
                // Update payment request
                paymentRequest.setRazorpayPaymentId(razorpayPaymentId);
                paymentRequest.setRazorpaySignature(razorpaySignature);
                paymentRequest.setStatus(PaymentStatus.PAID);
                paymentRequestRepository.save(paymentRequest);
                
                if (paymentRequest.getPurpose() == PaymentPurpose.WALLET_TOPUP) {

                    processWalletTopup(
                            paymentRequest,
                            razorpayPaymentId,
                            razorpayOrderId,
                            razorpaySignature);

                } else if (paymentRequest.getPurpose() == PaymentPurpose.CASE_PAYMENT) {

                    processCasePayment(
                            paymentRequest,
                            razorpayPaymentId,
                            razorpayOrderId,
                            razorpaySignature);
                }
                
                log.info("=== Payment processed successfully ===");
               
                return "Payment processed successfully";
                        
            } catch (Exception e) {
                // Create failed transaction record
                createFailedTransaction(razorpayPaymentId, razorpayOrderId, razorpaySignature,
                    "Transaction processing failed: " + e.getMessage());
                throw e;
            }
            
        } catch (Exception e) {
            log.error("Payment verification failed: {}", e.getMessage(), e);
            throw new RuntimeException("Payment verification failed: " + e.getMessage());
        }
    }

    private void processWalletTopup(
            PaymentRequest paymentRequest,
            String razorpayPaymentId,
            String razorpayOrderId,
            String razorpaySignature) {

        Wallet wallet = walletRepository
                .findByCompanyId(paymentRequest.getCompanyId())
                .orElseGet(() -> createWallet(paymentRequest.getCompanyId()));

        String transactionRef =
                "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        WalletTransaction transaction = WalletTransaction.builder()
                .transactionRef(transactionRef)
                .wallet(wallet)
                .amount(paymentRequest.getAmount())
                .transactionType(TransactionType.CREDIT)
                .status(TransactionStatus.SUCCESS)
                .description("Wallet Topup")
                .paymentGatewayRef(razorpayPaymentId)
                .razorpayOrderId(razorpayOrderId)
                .razorpayPaymentId(razorpayPaymentId)
                .razorpaySignature(razorpaySignature)
                .build();

        walletTransactionRepository.save(transaction);

        wallet.credit(paymentRequest.getAmount());

        walletRepository.save(wallet);

        log.info("Wallet credited successfully : {}",
                paymentRequest.getAmount());
    }
    
    private void processCasePayment(
            PaymentRequest paymentRequest,
            String razorpayPaymentId,
            String razorpayOrderId,
            String razorpaySignature) {

        log.info("Processing CASE_PAYMENT");
        
        
        VerificationCase verificationCase = verificationCaseRepository.findById(paymentRequest.getCaseId()).orElseGet(null);
        
        Invoice invoice = invoiceRepository
                .findByInvoiceNumberOrCaseId(
                        paymentRequest.getInvoiceNumber(),
                        paymentRequest.getCaseId())
                .orElseThrow(() ->
                        new RuntimeException("Invoice not found"));
        
        paymentRequest.setInvoiceId(invoice.getId());

        CasePayment casePayment = CasePayment.builder()
                .verificationCase(verificationCase)
                .invoice(invoice)
                .amount(
                        paymentRequest.getOnlineAmount() != null
                                ? paymentRequest.getOnlineAmount()
                                : paymentRequest.getAmount())
                .paymentMethod(PaymentMethod.RAZORPAY)
                .paymentReference(razorpayPaymentId)
                .status(PaymentStatus.PAID)
                .paidAt(LocalDateTime.now())
                .build();

        casePaymentRepository.save(casePayment);

        log.info(
                "Case payment saved. CaseId={}, InvoiceId={}, Amount={}",
                paymentRequest.getCaseId(),
                paymentRequest.getInvoiceId(),
                casePayment.getAmount());

        if (invoice != null) {

                        invoice.setStatus(InvoiceStatus.PAID);

                        invoice.setPaidAt(LocalDateTime.now());
                        invoice.setPaidAmount(casePayment.getAmount());

                        invoiceRepository.save(invoice);

                        log.info(
                                "Invoice marked as PAID : {}",
                                invoice.getInvoiceNumber());
                    
        }
    }

private void createFailedTransaction(String razorpayPaymentId, String razorpayOrderId, 
                                     String razorpaySignature, String errorMessage) {
    try {
        log.info("Creating failed transaction record for payment ID: {}", razorpayPaymentId);
        
        // Try to find payment request to get wallet info
        Optional<PaymentRequest> paymentRequestOpt = paymentRequestRepository
                .findByRazorpayOrderId(razorpayOrderId);
        
        if (paymentRequestOpt.isPresent()) {
            PaymentRequest paymentRequest = paymentRequestOpt.get();
            
            // Get or create user wallet
            Wallet wallet = walletRepository
                    .findByCompanyId(paymentRequest.getCompanyId())
                    .orElseGet(() -> createWallet(paymentRequest.getCompanyId()));
            
            String transactionRef = "TXN-FAIL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            WalletTransaction failedTransaction = WalletTransaction.builder()
                    .transactionRef(transactionRef)
                    .wallet(wallet)
                    .amount(paymentRequest.getAmount())
                    .transactionType(TransactionType.CREDIT)
                    .status(TransactionStatus.FAILED)
                    .description("Wallet top-up failed - " + errorMessage)
                    .paymentGatewayRef(razorpayPaymentId)
                    .razorpayOrderId(razorpayOrderId)
                    .razorpayPaymentId(razorpayPaymentId)
                    .razorpaySignature(razorpaySignature)
                    .metadata(String.format("{\"error\": \"%s\", \"timestamp\": \"%s\"}", 
                            errorMessage, LocalDateTime.now()))
                    .build();
            
            walletTransactionRepository.save(failedTransaction);
            log.info("Created failed transaction record: {}", transactionRef);
        }
    } catch (Exception ex) {
        log.error("Failed to create failed transaction record: {}", ex.getMessage());
    }
}
    
    
    /**
     * Get wallet balance
     */
@Transactional(readOnly = true)
public WalletBalanceResponseDto getWalletBalance(Long companyId) {
    
    // Find company first
    Optional<Company> companyOptional = companyRepository.findById(companyId);
    
    // Check if company exists
    if (companyOptional.isEmpty()) {
        throw new EntityNotFoundException("Company not found with ID: " + companyId);
    }
    
    Company company = companyOptional.get();
    
    /*
    
    // Check if company type is "default"
    if ("default".equalsIgnoreCase(company.getCompanyType())) {
        // For "default" company type, return a response without creating wallet
        return WalletBalanceResponseDto.builder()
               
                .currency("INR")
                .isActive(false)
                .build();
    }
    
    */
    
    // For non-default company types, get or create wallet
   Wallet wallet = walletRepository
            .findByCompanyId(companyId)
            .orElseGet(() -> createWallet(companyId));
    
    return WalletBalanceResponseDto.builder()
            .walletId(wallet.getWalletId())
            .userId(wallet.getUserId())
            .balance(wallet.getBalance())
            .currency(wallet.getCurrency())
            .isActive(wallet.getIsActive())
            .build();
}
    /**
     * Get wallet transactions
     */
    @Transactional(readOnly = true)
    public Page<WalletTransactionResponseDto> getWalletTransactions(Long companyId, Pageable pageable) {
        
        // First get the wallet for the company
        Wallet wallet = walletRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new EntityNotFoundException(
                    String.format("Wallet not found for company ID: %d", companyId)
                ));
        
        Long walletId = wallet.getWalletId();
        
        log.info("Fetching transactions for walletId: {} (companyId: {})", walletId, companyId);
        
        // Fetch transactions for this specific wallet
        return walletTransactionRepository.findByWalletId(walletId, pageable)
                .map(this::convertToTransactionResponseDto);
    }
    
    /**
     * Make payment from wallet
     */
    @Transactional
    public WalletTransactionResponseDto makePaymentFromWallet(CreatePaymentRequestDto requestDto) {

        log.info("Wallet payment request received: {}", requestDto);

        Wallet wallet = walletRepository.findByCompanyId(requestDto.getCompanyId())
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found for company: "
                                + requestDto.getCompanyId()));

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() ->
                        new RuntimeException("User not found: "
                                + requestDto.getUserId()));

        BigDecimal amount = requestDto.getAmount();

        if (!wallet.hasSufficientBalance(amount)) {
            throw new RuntimeException(
                    "Insufficient wallet balance. Available: "
                            + wallet.getBalance());
        }

        // Debit wallet
        wallet.debit(amount);
        walletRepository.save(wallet);

        // Create transaction
        String transactionRef =
                "PAY-" + UUID.randomUUID().toString()
                        .substring(0, 8)
                        .toUpperCase();

        WalletTransaction transaction = WalletTransaction.builder()
                .transactionRef(transactionRef)
                .wallet(wallet)
                .user(user)
                .amount(amount)
                .transactionType(TransactionType.DEBIT)
                .status(TransactionStatus.SUCCESS)
                .description(requestDto.getDescription())
                .metadata(
                        String.format(
                                "{\"caseId\":%s,\"invoiceNumber\":\"%s\",\"purpose\":\"%s\",\"paymentMethod\":\"WALLET\"}",
                                requestDto.getCaseId(),
                                requestDto.getInvoiceNumber(),
                                requestDto.getPurpose()))
                .build();

        transaction = walletTransactionRepository.save(transaction);

        log.info("Wallet debited successfully");
        log.info("Transaction Ref : {}", transactionRef);
        log.info("Remaining Balance : {}", wallet.getBalance());

        // ======================================
        // CASE PAYMENT SPECIFIC LOGIC
        // ======================================

        if (PaymentPurpose.CASE_PAYMENT.equals(requestDto.getPurpose())) {

            log.info("Processing case payment");

            Invoice invoice = invoiceRepository
                    .findByInvoiceNumberOrCaseId(
                    		requestDto.getInvoiceNumber(),
                    		requestDto.getCaseId())
                    .orElseThrow(() ->
                            new RuntimeException("Invoice not found"));

            invoice.setStatus(InvoiceStatus.PAID);
            invoice.setPaidAt(LocalDateTime.now());

            invoiceRepository.save(invoice);

            log.info("Invoice marked PAID : {}",
                    invoice.getInvoiceNumber());

            VerificationCase verificationCase = verificationCaseRepository.findById(requestDto.getCaseId()).orElseGet(null);
            
            
           // Optional: create CasePayment table entry

            CasePayment payment = CasePayment.builder()
                    .verificationCase(verificationCase)
                    .invoice(invoice)
                    .amount(amount)
                    .paymentMethod(PaymentMethod.WALLET)
                    .paymentReference(transactionRef)
                    .status(PaymentStatus.PAID)
                    .paidAt(LocalDateTime.now())
                    .build();

            casePaymentRepository.save(payment);
            
            List<VerificationCaseCheck> caseChecks = verificationCaseCheckRepository
    				.findByVerificationCase_CaseId(verificationCase.getCaseId());
            vendorAssignmentService.assignVendorsToCaseChecks(caseChecks);
            
            verificationCase.setStatus(CaseStatus.IN_PROGRESS);
            
            verificationCaseRepository.save(verificationCase);
            
        }

        return convertToTransactionResponseDto(
                transaction,
                wallet.getBalance());
    }
    
    /**
     * Create wallet for user if not exists
     */
    private Wallet createWallet(Long companyId) {
        Wallet wallet = Wallet.builder()
                .userId(SecurityUtils.getCurrentUserId())
                .companyId(companyId)
                .balance(BigDecimal.ZERO)
                .currency("INR")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
        
        return walletRepository.save(wallet);
    }
    
    /**
     * Generate Razorpay signature (simplified - use Razorpay's utility in production)
     */
    private String generateSignature(String data) {
        try {
            
            String secret = this.razorpayKeySecret;

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            // Convert to lowercase hex
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                result.append(String.format("%02x", b));
            }
            
            log.info("Generated HMAC SHA256 signature: {}", result.toString());
            return result.toString();
            
        } catch (Exception e) {
            log.error("Error generating signature", e);
            throw new RuntimeException("Signature generation failed", e);
        }
    }
    
    /**
     * Convert entity to response DTO
     */
    private WalletTransactionResponseDto convertToTransactionResponseDto(WalletTransaction transaction) {
        return convertToTransactionResponseDto(transaction, null);
    }
    
    private WalletTransactionResponseDto convertToTransactionResponseDto(WalletTransaction transaction, BigDecimal balanceAfter) {
        return WalletTransactionResponseDto.builder()
                .transactionId(transaction.getTransactionId())
                .transactionRef(transaction.getTransactionRef())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .status(transaction.getStatus())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .balanceAfterTransaction(balanceAfter)
                .build();
    }
    
    private Long getLong(JSONObject obj, String key) {
        return obj.has(key) && obj.get(key) != null
                ? ((Number) obj.get(key)).longValue()
                : null;
    }
    
    private Integer getInt(JSONObject obj, String key) {
        return obj.has(key) && obj.get(key) != null
                ? ((Number) obj.get(key)).intValue()
                : null;
    }
    private Long getEpochSeconds(Object value) {
        if (value == null) return null;

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        if (value instanceof Date) {
            return ((Date) value).getTime() / 1000; // convert ms → seconds
        }

        throw new IllegalArgumentException(
            "Unsupported type for epoch time: " + value.getClass()
        );
    }
}
