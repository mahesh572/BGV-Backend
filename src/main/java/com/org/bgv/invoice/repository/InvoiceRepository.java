package com.org.bgv.invoice.repository;

import com.org.bgv.entity.VerificationCase;
import com.org.bgv.invoice.entity.Invoice;
import com.org.bgv.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    // =========================
    // FIND BY RELATIONSHIPS
    // =========================
    
    Optional<Invoice> findByVerificationCase(VerificationCase verificationCase);
    
    @Query("SELECT i FROM Invoice i WHERE i.verificationCase.caseId = :caseId")
    Optional<Invoice> findByCaseId(@Param("caseId") Long caseId);
    
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    // =========================
    // FIND BY COMPANY (FIXED - using company.id)
    // =========================
    
    List<Invoice> findByCompanyId(Long companyId);
    
    Page<Invoice> findByCompanyId(Long companyId, Pageable pageable);
    
    // =========================
    // FIND BY STATUS
    // =========================
    
    List<Invoice> findByStatus(InvoiceStatus status);
    
    Page<Invoice> findByStatus(InvoiceStatus status, Pageable pageable);
    
    @Query("SELECT i FROM Invoice i WHERE i.company.id = :companyId AND i.status = :status")
    List<Invoice> findByCompanyIdAndStatus(@Param("companyId") Long companyId, @Param("status") InvoiceStatus status);
    
    // =========================
    // FIND BY DATE RANGE
    // =========================
    
    List<Invoice> findByInvoiceDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT i FROM Invoice i WHERE i.company.id = :companyId AND i.invoiceDate BETWEEN :startDate AND :endDate")
    List<Invoice> findByCompanyIdAndInvoiceDateBetween(@Param("companyId") Long companyId, 
                                                        @Param("startDate") LocalDateTime startDate, 
                                                        @Param("endDate") LocalDateTime endDate);
    
    // =========================
    // OVERDUE INVOICES
    // =========================
    
    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :now AND i.status = :status")
    List<Invoice> findOverdueInvoices(@Param("now") LocalDateTime now, 
                                       @Param("status") InvoiceStatus status);
    
    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :now AND i.status = :status AND i.company.id = :companyId")
    List<Invoice> findOverdueInvoicesByCompany(@Param("now") LocalDateTime now, 
                                                @Param("status") InvoiceStatus status,
                                                @Param("companyId") Long companyId);
    
    // =========================
    // EXISTS CHECKS
    // =========================
    
    boolean existsByVerificationCase(VerificationCase verificationCase);
    
    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Invoice i WHERE i.verificationCase.caseId = :caseId")
    boolean existsByCaseId(@Param("caseId") Long caseId);
    
    boolean existsByInvoiceNumber(String invoiceNumber);
    
    // =========================
    // COUNT METHODS (FIXED)
    // =========================
    
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.company.id = :companyId")
    long countByCompanyId(@Param("companyId") Long companyId);
    
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.company.id = :companyId AND i.status = :status")
    long countByCompanyIdAndStatus(@Param("companyId") Long companyId, @Param("status") InvoiceStatus status);
    
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = :status")
    long countByStatus(@Param("status") InvoiceStatus status);
    
    // =========================
    // SUM/AGGREGATION METHODS (FIXED)
    // =========================
    
    @Query("SELECT COALESCE(SUM(i.grandTotal), 0) FROM Invoice i WHERE i.status = 'PAID' AND i.company.id = :companyId")
    BigDecimal getTotalPaidRevenueByCompany(@Param("companyId") Long companyId);
    
    @Query("SELECT COALESCE(SUM(i.grandTotal), 0) FROM Invoice i WHERE i.status = 'PAID' AND i.paidAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(i.grandTotal), 0) FROM Invoice i WHERE i.status = 'PENDING' AND i.company.id = :companyId")
    BigDecimal getTotalPendingAmountByCompany(@Param("companyId") Long companyId);
    
    @Query("SELECT COALESCE(SUM(i.grandTotal), 0) FROM Invoice i WHERE i.status = 'OVERDUE' AND i.company.id = :companyId")
    BigDecimal getTotalOverdueAmountByCompany(@Param("companyId") Long companyId);
    
    // =========================
    // GROUP BY METHODS (FIXED)
    // =========================
    
    @Query("SELECT i.status, COUNT(i), COALESCE(SUM(i.grandTotal), 0) FROM Invoice i WHERE i.company.id = :companyId GROUP BY i.status")
    List<Object[]> getInvoiceSummaryByCompany(@Param("companyId") Long companyId);
    
    @Query("SELECT FUNCTION('DATE_FORMAT', i.invoiceDate, '%Y-%m'), COALESCE(SUM(i.grandTotal), 0) FROM Invoice i " +
           "WHERE i.status = 'PAID' AND i.company.id = :companyId " +
           "GROUP BY FUNCTION('DATE_FORMAT', i.invoiceDate, '%Y-%m') ORDER BY FUNCTION('DATE_FORMAT', i.invoiceDate, '%Y-%m') DESC")
    List<Object[]> getMonthlyRevenueByCompany(@Param("companyId") Long companyId);
    
    // =========================
    // ADMIN METHODS (NO COMPANY FILTER)
    // =========================
    
    @Query("SELECT i FROM Invoice i WHERE " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:companyId IS NULL OR i.company.id = :companyId)")
    Page<Invoice> findAllWithFilters(@Param("status") InvoiceStatus status,
                                      @Param("companyId") Long companyId,
                                      Pageable pageable);
    
    @Query("SELECT COALESCE(SUM(i.grandTotal), 0) FROM Invoice i WHERE i.status = 'PAID'")
    BigDecimal getTotalSystemRevenue();
    
    @Query("SELECT i.status, COUNT(i), COALESCE(SUM(i.grandTotal), 0) FROM Invoice i GROUP BY i.status")
    List<Object[]> getSystemWideInvoiceSummary();
}