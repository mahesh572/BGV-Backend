package com.org.bgv.invoice.service;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.repository.CandidateRepository;
import com.org.bgv.entity.*;
import com.org.bgv.enums.InvoiceStatus;
import com.org.bgv.invoice.dto.*;
import com.org.bgv.invoice.entity.Invoice;
import com.org.bgv.invoice.entity.InvoiceItem;
import com.org.bgv.invoice.repository.InvoiceItemRepository;
import com.org.bgv.invoice.repository.InvoiceRepository;
import com.org.bgv.repository.*;
import com.org.bgv.service.ReferenceNumberGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceGenerationService {

    private final VerificationCaseRepository verificationCaseRepository;
    private final VerificationCaseSelectionRepository verificationCaseSelectionRepository;
    private final CompanyRepository companyRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final CandidateRepository candidateRepository;
    private final ReferenceNumberGenerator referenceNumberGenerator;

    // =========================
    // INVOICE GENERATION & BASIC CRUD
    // =========================

    @Transactional
    public InvoiceDTO generateInvoice(Long caseId) {
        VerificationCase verificationCase = verificationCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found: " + caseId));
        
     // VALIDATION: Check if pricing is confirmed
        if (!Boolean.TRUE.equals(verificationCase.getPricingConfirmed())) {
            throw new RuntimeException("Pricing must be confirmed before generating invoice");
        }

        // Check if invoice already exists
        Optional<Invoice> existingInvoice = invoiceRepository.findByVerificationCase(verificationCase);
        if (existingInvoice.isPresent()) {
            log.info("Invoice already exists for case: {}, returning existing", caseId);
            return convertToDTO(existingInvoice.get());
        }

        // Get all selections for this case
        List<VerificationCaseSelection> selections = verificationCaseSelectionRepository
                .findByVerificationCase(verificationCase);

        // Separate base and add-on selections
        List<VerificationCaseSelection> baseSelections = selections.stream()
                .filter(s -> Boolean.TRUE.equals(s.getIncludedInBase()))
                .collect(Collectors.toList());

        List<VerificationCaseSelection> addonSelections = selections.stream()
                .filter(s -> Boolean.FALSE.equals(s.getIncludedInBase()))
                .filter(s -> s.getUnitPrice() != null && s.getUnitPrice().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        // Calculate totals
        BigDecimal baseTotal = verificationCase.getBasePrice();
        BigDecimal addonTotal = addonSelections.stream()
                .map(VerificationCaseSelection::getUnitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal grandTotal = baseTotal.add(addonTotal);
        grandTotal = grandTotal.add(calculateTax(addonTotal));

        Company company = companyRepository.findById(verificationCase.getCompanyId()).orElse(null);
        Candidate candidate = candidateRepository.findByCompanyIdAndCandidateId(verificationCase.getCompanyId(), 
                verificationCase.getCandidateId()).orElse(null);

        // Create invoice
        Invoice invoice = Invoice.builder()
                .invoiceNumber(generateInvoiceNumber(verificationCase))
                .verificationCase(verificationCase)
                .company(company)
                .candidate(candidate)
                .invoiceDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(30))
                .baseTotal(baseTotal)
                .addonTotal(addonTotal)
                .taxAmount(calculateTax(addonTotal))
                .grandTotal(grandTotal)
                .status(InvoiceStatus.PENDING)
                .currency("INR")
                .build();

        invoice = invoiceRepository.save(invoice);

        // Create invoice items
        List<InvoiceItem> allItems = new ArrayList<>();
        
        for (VerificationCaseSelection selection : addonSelections) {
            InvoiceItem invoiceItem = createInvoiceItem(invoice, selection);
            allItems.add(invoiceItem);
        }

        for (VerificationCaseSelection selection : baseSelections) {
            InvoiceItem invoiceItem = createBaseInvoiceItem(invoice, selection);
            allItems.add(invoiceItem);
        }
        
        invoiceItemRepository.saveAll(allItems);
        
        verificationCase.setInvoiceGenerated(true);
        verificationCase.setInvoiceGeneratedAt(LocalDateTime.now());
        verificationCase.setAddonPrice(addonTotal);
        verificationCase.setTotalPrice(grandTotal);
        verificationCaseRepository.save(verificationCase);

        log.info("Invoice generated for case {}: {} - Total: {}", 
                caseId, invoice.getInvoiceNumber(), invoice.getGrandTotal());
       log.info("Case updated - invoiceGenerated: {}, invoiceGeneratedAt: {}", 
                verificationCase.getInvoiceGenerated(), verificationCase.getInvoiceGeneratedAt());

        return convertToDTO(invoice);
    }

    public InvoiceDTO getInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));
        return convertToDTO(invoice);
    }

    public InvoiceDTO getInvoiceByCaseId(Long caseId) {
        Invoice invoice = invoiceRepository.findByCaseId(caseId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for case: " + caseId));
        return convertToDTO(invoice);
    }

    public InvoiceDTO getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceNumber));
        return convertToDTO(invoice);
    }

    // =========================
    // GET INVOICES BY COMPANY (PAGINATED)
    // =========================
    
    public List<InvoiceDTO> getInvoicesByCompany(Long companyId) {
        List<Invoice> invoices = invoiceRepository.findByCompanyId(companyId);
        return invoices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<InvoiceDTO> getInvoicesByCompanyPaginated(Long companyId, Pageable pageable) {
        Page<Invoice> invoicePage = invoiceRepository.findByCompanyId(companyId, pageable);
        return invoicePage.map(this::convertToDTO);
    }

    // =========================
    // GET INVOICES BY STATUS
    // =========================
    
    public List<InvoiceDTO> getInvoicesByStatus(InvoiceStatus status) {
        List<Invoice> invoices = invoiceRepository.findByStatus(status);
        return invoices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<InvoiceDTO> getInvoicesByStatusPaginated(InvoiceStatus status, Pageable pageable) {
        Page<Invoice> invoicePage = invoiceRepository.findByStatus(status, pageable);
        return invoicePage.map(this::convertToDTO);
    }

    public List<InvoiceDTO> getInvoicesByCompanyAndStatus(Long companyId, InvoiceStatus status) {
        List<Invoice> invoices = invoiceRepository.findByCompanyIdAndStatus(companyId, status);
        return invoices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // =========================
    // GET INVOICES BY DATE RANGE
    // =========================
    
    public List<InvoiceDTO> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Invoice> invoices = invoiceRepository.findByInvoiceDateBetween(startDate, endDate);
        return invoices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<InvoiceDTO> getInvoicesByCompanyAndDateRange(Long companyId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Invoice> invoices = invoiceRepository.findByCompanyIdAndInvoiceDateBetween(companyId, startDate, endDate);
        return invoices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // =========================
    // OVERDUE INVOICES
    // =========================
    
    public List<InvoiceDTO> getOverdueInvoices() {
        List<Invoice> invoices = invoiceRepository.findOverdueInvoices(LocalDateTime.now(), InvoiceStatus.PENDING);
        return invoices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<InvoiceDTO> getOverdueInvoicesByCompany(Long companyId) {
        List<Invoice> invoices = invoiceRepository.findOverdueInvoicesByCompany(LocalDateTime.now(), InvoiceStatus.PENDING, companyId);
        return invoices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // =========================
    // INVOICE BREAKDOWN
    // =========================
    
    public InvoiceBreakdownDTO getInvoiceBreakdown(Long caseId) {
        VerificationCase verificationCase = verificationCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found: " + caseId));
        
        Company company = companyRepository.findById(verificationCase.getCompanyId()).orElse(null);
        Candidate candidate = candidateRepository.findByCompanyIdAndCandidateId(verificationCase.getCompanyId(), 
                verificationCase.getCandidateId()).orElse(null);

        List<VerificationCaseSelection> selections = verificationCaseSelectionRepository
                .findByVerificationCase(verificationCase);

        Map<String, CategoryInvoiceBreakdown> breakdownByCategory = new HashMap<>();

        for (VerificationCaseSelection selection : selections) {
            String categoryName = selection.getType().getName();
            CategoryInvoiceBreakdown breakdown = breakdownByCategory
                    .computeIfAbsent(categoryName, k -> new CategoryInvoiceBreakdown(categoryName));
            
            String description = getItemDescription(selection);
            BigDecimal price = selection.getUnitPrice() != null ? selection.getUnitPrice() : BigDecimal.ZERO;
            
            breakdown.addItem(description, price);
        }

        BigDecimal subtotal = calculateAddonTotal(selections);
        BigDecimal tax = calculateTax(subtotal);
        
        return InvoiceBreakdownDTO.builder()
                .caseId(caseId)
                .caseReferenceNumber(verificationCase.getCaseNumber())
                .candidateName(candidate != null ? candidate.getFirstName() : "N/A")
                .companyName(company != null ? company.getCompanyName() : "N/A")
                .breakdown(breakdownByCategory)
                .subtotal(subtotal)
                .tax(tax)
                .grandTotal(subtotal.add(tax))
                .build();
    }

    // =========================
    // INVOICE ACTIONS (PAY, CANCEL, ETC)
    // =========================
    
    @Transactional
    public InvoiceDTO markAsPaid(Long invoiceId, InvoicePaymentRequest paymentRequest) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));
        
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());
        invoice.setPaymentReference(paymentRequest.getPaymentReference());
        
        String notes = invoice.getNotes();
        if (notes == null) notes = "";
        notes += " Paid via: " + paymentRequest.getPaymentMethod() + " at " + LocalDateTime.now();
        invoice.setNotes(notes);
        
        invoice = invoiceRepository.save(invoice);
        
        log.info("Invoice {} marked as paid", invoice.getInvoiceNumber());
        
        return convertToDTO(invoice);
    }

    @Transactional
    public InvoiceDTO markAsOverdue(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));
        
        invoice.setStatus(InvoiceStatus.OVERDUE);
        invoice = invoiceRepository.save(invoice);
        
        log.info("Invoice {} marked as overdue", invoice.getInvoiceNumber());
        
        return convertToDTO(invoice);
    }

    @Transactional
    public InvoiceDTO cancelInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));
        
        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoice = invoiceRepository.save(invoice);
        
        log.info("Invoice {} cancelled", invoice.getInvoiceNumber());
        
        return convertToDTO(invoice);
    }

    // =========================
    // INVOICE SUMMARY & REPORTS
    // =========================
    
    public InvoiceSummaryDTO getInvoiceSummary(Long companyId) {
        Company company = companyRepository.findById(companyId).orElse(null);
        
        long totalInvoices = invoiceRepository.countByCompanyId(companyId);
        long paidInvoices = invoiceRepository.countByCompanyIdAndStatus(companyId, InvoiceStatus.PAID);
        long pendingInvoices = invoiceRepository.countByCompanyIdAndStatus(companyId, InvoiceStatus.PENDING);
        long overdueInvoices = invoiceRepository.countByCompanyIdAndStatus(companyId, InvoiceStatus.OVERDUE);
        long cancelledInvoices = invoiceRepository.countByCompanyIdAndStatus(companyId, InvoiceStatus.CANCELLED);
        
        BigDecimal totalRevenue = invoiceRepository.getTotalPaidRevenueByCompany(companyId);
        BigDecimal pendingAmount = invoiceRepository.getTotalPendingAmountByCompany(companyId);
        BigDecimal overdueAmount = invoiceRepository.getTotalOverdueAmountByCompany(companyId);
        
        // Get monthly revenue breakdown
        List<Object[]> monthlyData = invoiceRepository.getMonthlyRevenueByCompany(companyId);
        Map<String, BigDecimal> revenueByMonth = new LinkedHashMap<>();
        for (Object[] data : monthlyData) {
            revenueByMonth.put((String) data[0], (BigDecimal) data[1]);
        }
        
        return InvoiceSummaryDTO.builder()
                .companyId(companyId)
                .companyName(company != null ? company.getCompanyName() : "N/A")
                .totalInvoices((int) totalInvoices)
                .paidInvoices((int) paidInvoices)
                .pendingInvoices((int) pendingInvoices)
                .overdueInvoices((int) overdueInvoices)
                .cancelledInvoices((int) cancelledInvoices)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .pendingAmount(pendingAmount != null ? pendingAmount : BigDecimal.ZERO)
                .overdueAmount(overdueAmount != null ? overdueAmount : BigDecimal.ZERO)
                .revenueByMonth(revenueByMonth)
                .build();
    }

    // =========================
    // ADMIN METHODS
    // =========================
    
    public Page<InvoiceDTO> getAllInvoicesForAdmin(InvoiceFilterDTO filter, Pageable pageable) {
        Page<Invoice> invoicePage = invoiceRepository.findAllWithFilters(
                filter.getStatus(),
                filter.getCompanyId(),
                pageable
        );
        return invoicePage.map(this::convertToDTO);
    }

    public BigDecimal getTotalSystemRevenue() {
        return invoiceRepository.getTotalSystemRevenue();
    }

    // =========================
    // EXISTENCE CHECKS
    // =========================
    
    public boolean invoiceExistsForCase(Long caseId) {
        return invoiceRepository.existsByCaseId(caseId);
    }

    // =========================
    // REGENERATE INVOICE
    // =========================
    
    @Transactional
    public InvoiceDTO regenerateInvoice(Long caseId) {
        // Delete existing invoice if exists
        if (invoiceRepository.existsByCaseId(caseId)) {
            Optional<Invoice> existingInvoice = invoiceRepository.findByCaseId(caseId);
            if (existingInvoice.isPresent()) {
                Invoice invoice = existingInvoice.get();
                invoiceItemRepository.deleteByInvoice(invoice);
                invoiceRepository.delete(invoice);
                log.info("Deleted existing invoice for case: {}", caseId);
            }
        }
        
        // Generate new invoice
        return generateInvoice(caseId);
    }

    // =========================
    // PRIVATE HELPER METHODS
    // =========================
    
    private InvoiceItem createInvoiceItem(Invoice invoice, VerificationCaseSelection selection) {
        String description = getItemDescription(selection);
        
        return InvoiceItem.builder()
                .invoice(invoice)
                .selectionId(selection.getId())
                .category(selection.getType().name())
                .description(description)
                .quantity(BigDecimal.ONE)
                .unitPrice(selection.getUnitPrice())
                .totalPrice(selection.getUnitPrice())
                .build();
    }

    private InvoiceItem createBaseInvoiceItem(Invoice invoice, VerificationCaseSelection selection) {
        String description = getItemDescription(selection);
        
        return InvoiceItem.builder()
                .invoice(invoice)
                .selectionId(selection.getId())
                .category(selection.getType().name())
                .description(description + " (Included in Base Package)")
                .quantity(BigDecimal.ONE)
                .unitPrice(BigDecimal.ZERO)
                .totalPrice(BigDecimal.ZERO)
                .build();
    }

    private String getItemDescription(VerificationCaseSelection selection) {
        switch (selection.getType()) {
            case EDUCATION:
                return "Education Verification";
            case WORK:
                return "Employment Verification";
            case IDENTITY:
                return "Identity Verification";
            case ADDRESS:
                return "Address Verification";
            default:
                return "Verification Service";
        }
    }

    private String generateInvoiceNumber(VerificationCase verificationCase) {
        
        return referenceNumberGenerator.generateInvoiceNumber();
        
    }

    private BigDecimal calculateAddonTotal(List<VerificationCaseSelection> selections) {
        return selections.stream()
                .filter(s -> Boolean.FALSE.equals(s.getIncludedInBase()))
                .map(VerificationCaseSelection::getUnitPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTax(BigDecimal amount) {
        BigDecimal taxRate = new BigDecimal("0.18");
        return amount.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
    }

    private InvoiceDTO convertToDTO(Invoice invoice) {
        List<InvoiceItem> items = invoiceItemRepository.findByInvoice(invoice);
        
        List<InvoiceItemDTO> itemDTOs = items.stream()
                .map(this::convertToItemDTO)
                .collect(Collectors.toList());
        
        return InvoiceDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .caseId(invoice.getVerificationCase() != null ? invoice.getVerificationCase().getCaseId() : null)
                .caseReferenceNumber(invoice.getVerificationCase() != null ? invoice.getVerificationCase().getCaseNumber() : null)
                .companyId(invoice.getCompany() != null ? invoice.getCompany().getId() : null)  // FIXED: using getId()
                .companyName(invoice.getCompany() != null ? invoice.getCompany().getCompanyName() : null)
                .candidateId(invoice.getCandidate() != null ? invoice.getCandidate().getCandidateId() : null)  // FIXED: using getCandidateId()
                .candidateName(invoice.getCandidate() != null ? invoice.getCandidate().getFirstName() : null)
                .invoiceDate(invoice.getInvoiceDate())
                .dueDate(invoice.getDueDate())
                .baseTotal(invoice.getBaseTotal())
                .addonTotal(invoice.getAddonTotal())
                .taxAmount(invoice.getTaxAmount())
                .grandTotal(invoice.getGrandTotal())
                .currency(invoice.getCurrency())
                .status(invoice.getStatus().name())
                .paidAt(invoice.getPaidAt())
                .paymentReference(invoice.getPaymentReference())
                .notes(invoice.getNotes())
                .items(itemDTOs)
                .build();
    }

    private InvoiceItemDTO convertToItemDTO(InvoiceItem item) {
        return InvoiceItemDTO.builder()
                .id(item.getId())
                .selectionId(item.getSelectionId())
                .category(item.getCategory())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}