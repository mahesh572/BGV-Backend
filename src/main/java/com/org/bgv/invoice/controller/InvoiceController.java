package com.org.bgv.invoice.controller;


import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.enums.InvoiceStatus;
import com.org.bgv.invoice.dto.InvoiceBreakdownDTO;
import com.org.bgv.invoice.dto.InvoiceDTO;
import com.org.bgv.invoice.dto.InvoiceFilterDTO;
import com.org.bgv.invoice.dto.InvoicePaymentRequest;
import com.org.bgv.invoice.dto.InvoiceSummaryDTO;
import com.org.bgv.invoice.service.InvoiceGenerationService;
import com.org.bgv.invoice.service.InvoicePdfGeneratorService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceGenerationService invoiceService;
    private final InvoicePdfGeneratorService invoicePdfGeneratorService;

    // =========================
    // GENERATE INVOICE
    // =========================
    @PostMapping("/generate/{caseId}")
    public ResponseEntity<CustomApiResponse<InvoiceDTO>> generateInvoice(@PathVariable Long caseId) {
        InvoiceDTO invoice = invoiceService.generateInvoice(caseId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomApiResponse.success("Invoice generated successfully", invoice, HttpStatus.CREATED));
    }

    // =========================
    // GET INVOICE BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse<InvoiceDTO>> getInvoiceById(@PathVariable Long id) {
        InvoiceDTO invoice = invoiceService.getInvoiceById(id);
        
        return ResponseEntity.ok(
                CustomApiResponse.success("Invoice fetched successfully", invoice, HttpStatus.OK)
        );
    }

    // =========================
    // GET INVOICE BY CASE ID
    // =========================
    @GetMapping("/case/{caseId}")
    public ResponseEntity<CustomApiResponse<InvoiceDTO>> getInvoiceByCaseId(@PathVariable Long caseId) {
        InvoiceDTO invoice = invoiceService.getInvoiceByCaseId(caseId);
        
        return ResponseEntity.ok(
                CustomApiResponse.success("Invoice fetched successfully", invoice, HttpStatus.OK)
        );
    }

    // =========================
    // GET INVOICE BY INVOICE NUMBER
    // =========================
    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<CustomApiResponse<InvoiceDTO>> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        InvoiceDTO invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
        
        return ResponseEntity.ok(
                CustomApiResponse.success("Invoice fetched successfully", invoice, HttpStatus.OK)
        );
    }

    // =========================
    // GET INVOICE BREAKDOWN
    // =========================
    @GetMapping("/{id}/breakdown")
    public ResponseEntity<CustomApiResponse<InvoiceBreakdownDTO>> getInvoiceBreakdown(@PathVariable Long id) {
        InvoiceBreakdownDTO breakdown = invoiceService.getInvoiceBreakdown(id);
        
        return ResponseEntity.ok(
                CustomApiResponse.success("Invoice breakdown fetched successfully", breakdown, HttpStatus.OK)
        );
    }

    // =========================
    // GET ALL INVOICES FOR COMPANY
    // =========================
    @GetMapping("/company/{companyId}")
    public ResponseEntity<CustomApiResponse<List<InvoiceDTO>>> getInvoicesByCompany(@PathVariable Long companyId) {
        List<InvoiceDTO> invoices = invoiceService.getInvoicesByCompany(companyId);
        
        return ResponseEntity.ok(
                CustomApiResponse.success("Invoices fetched successfully", invoices, HttpStatus.OK)
        );
    }

    // =========================
    // GET INVOICES FOR COMPANY WITH PAGINATION
    // =========================
    @GetMapping("/company/{companyId}/paginated")
    public ResponseEntity<CustomApiResponse<Page<InvoiceDTO>>> getInvoicesByCompanyPaginated(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "invoiceDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<InvoiceDTO> invoices = invoiceService.getInvoicesByCompanyPaginated(companyId, pageable);
        
        return ResponseEntity.ok(
                CustomApiResponse.success("Invoices fetched successfully", invoices, HttpStatus.OK)
        );
    }

    // =========================
    // GET INVOICES BY STATUS
    // =========================
    @GetMapping("/status/{status}")
    public ResponseEntity<CustomApiResponse<List<InvoiceDTO>>> getInvoicesByStatus(@PathVariable InvoiceStatus status) {
        List<InvoiceDTO> invoices = invoiceService.getInvoicesByStatus(status);
        
        return ResponseEntity.ok(
                CustomApiResponse.success("Invoices fetched successfully", invoices, HttpStatus.OK)
        );
    }

    // =========================
    // GET INVOICES BY DATE RANGE
    // =========================
    @GetMapping("/date-range")
    public ResponseEntity<CustomApiResponse<List<InvoiceDTO>>> getInvoicesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        List<InvoiceDTO> invoices = invoiceService.getInvoicesByDateRange(startDateTime, endDateTime);
        
        return ResponseEntity.ok(
                CustomApiResponse.success("Invoices fetched successfully", invoices, HttpStatus.OK)
        );
    }

    // =========================
    // GET INVOICE SUMMARY FOR COMPANY
    // =========================
    @GetMapping("/company/{companyId}/summary")
    public ResponseEntity<CustomApiResponse<InvoiceSummaryDTO>> getInvoiceSummary(@PathVariable Long companyId) {
        InvoiceSummaryDTO summary = invoiceService.getInvoiceSummary(companyId);
        
        return ResponseEntity.ok(
                CustomApiResponse.success("Invoice summary fetched successfully", summary, HttpStatus.OK)
        );
    }

    // =========================
    // GET OVERDUE INVOICES
    // =========================
    @GetMapping("/overdue")
    public ResponseEntity<CustomApiResponse<List<InvoiceDTO>>> getOverdueInvoices() {
        List<InvoiceDTO> invoices = invoiceService.getOverdueInvoices();
        
        return ResponseEntity.ok(
                CustomApiResponse.success("Overdue invoices fetched successfully", invoices, HttpStatus.OK)
        );
    }

    // =========================
    // MARK INVOICE AS PAID
    // =========================
    @PostMapping("/{id}/pay")
    public ResponseEntity<CustomApiResponse<InvoiceDTO>> markAsPaid(
            @PathVariable Long id,
            @RequestBody InvoicePaymentRequest paymentRequest) {
        
        InvoiceDTO invoice = invoiceService.markAsPaid(id, paymentRequest);
        
        return ResponseEntity.ok(
                CustomApiResponse.success("Invoice marked as paid successfully", invoice, HttpStatus.OK)
        );
    }

    // =========================
    // MARK INVOICE AS OVERDUE
    // =========================
    @PostMapping("/{id}/overdue")
    public ResponseEntity<CustomApiResponse<InvoiceDTO>> markAsOverdue(@PathVariable Long id) {
        InvoiceDTO invoice = invoiceService.markAsOverdue(id);
        
        return ResponseEntity.ok(
                CustomApiResponse.success("Invoice marked as overdue successfully", invoice, HttpStatus.OK)
        );
    }

    // =========================
    // CANCEL INVOICE
    // =========================
    @PostMapping("/{id}/cancel")
    public ResponseEntity<CustomApiResponse<InvoiceDTO>> cancelInvoice(@PathVariable Long id) {
        InvoiceDTO invoice = invoiceService.cancelInvoice(id);
        
        return ResponseEntity.ok(
                CustomApiResponse.success("Invoice cancelled successfully", invoice, HttpStatus.OK)
        );
    }

    // =========================
    // DOWNLOAD INVOICE PDF
    // =========================
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable Long id) {
        
        
        InvoiceDTO invoice = invoiceService.getInvoiceById(id);
        
        byte[] pdfContent = invoicePdfGeneratorService.generateInvoicePdf(invoice);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice_" + invoice.getInvoiceNumber() + ".pdf");
        headers.setContentLength(pdfContent.length);
        
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
    /*
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable Long id) {
        byte[] pdfContent = invoiceGenerationService.generateInvoicePdf(id);
        
        InvoiceDTO invoice = invoiceGenerationService.getInvoiceById(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice_" + invoice.getInvoiceNumber() + ".pdf");
        headers.setContentLength(pdfContent.length);
        
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
*/
    // =========================
    // SEND INVOICE EMAIL
    // =========================
    @PostMapping("/{id}/send-email")
    public ResponseEntity<CustomApiResponse<Void>> sendInvoiceEmail(@PathVariable Long id) {
      //  invoiceService.sendInvoiceEmail(id);
        
        return ResponseEntity.ok(
                CustomApiResponse.success("Invoice email sent successfully", null, HttpStatus.OK)
        );
    }

    // =========================
    // ADMIN: GET ALL INVOICES
    // =========================
    @GetMapping("/admin/all")
    public ResponseEntity<CustomApiResponse<Page<InvoiceDTO>>> getAllInvoicesForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) Long companyId,
            @RequestParam(defaultValue = "invoiceDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        InvoiceFilterDTO filter = InvoiceFilterDTO.builder()
                .status(status)
                .companyId(companyId)
                .build();
        
        Page<InvoiceDTO> invoices = invoiceService.getAllInvoicesForAdmin(filter, pageable);
        
        return ResponseEntity.ok(
                CustomApiResponse.success("Invoices fetched successfully", invoices, HttpStatus.OK)
        );
    }

    // =========================
    // ADMIN: GET REVENUE REPORT
    // =========================
   /*
    
    @GetMapping("/admin/revenue")
    public ResponseEntity<CustomApiResponse<Object>> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long companyId) {
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        Object revenueReport = invoiceService.getRevenueReport(startDateTime, endDateTime, companyId);
        
        return ResponseEntity.ok(
                CustomApiResponse.success("Revenue report fetched successfully", revenueReport, HttpStatus.OK)
        );
    }
*/
    // =========================
    // CHECK IF INVOICE EXISTS FOR CASE
    // =========================
    @GetMapping("/exists/{caseId}")
    public ResponseEntity<CustomApiResponse<Boolean>> checkInvoiceExists(@PathVariable Long caseId) {
        boolean exists = invoiceService.invoiceExistsForCase(caseId);
        
        return ResponseEntity.ok(
                CustomApiResponse.success("Invoice existence checked successfully", exists, HttpStatus.OK)
        );
    }

    // =========================
    // REGENERATE INVOICE (for admin)
    // =========================
    @PostMapping("/admin/regenerate/{caseId}")
    public ResponseEntity<CustomApiResponse<InvoiceDTO>> regenerateInvoice(@PathVariable Long caseId) {
        InvoiceDTO invoice = invoiceService.regenerateInvoice(caseId);
        
        return ResponseEntity.ok(
                CustomApiResponse.success("Invoice regenerated successfully", invoice, HttpStatus.OK)
        );
    }
}