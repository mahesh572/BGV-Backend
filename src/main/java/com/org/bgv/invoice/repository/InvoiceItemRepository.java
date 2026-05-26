package com.org.bgv.invoice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.invoice.entity.Invoice;
import com.org.bgv.invoice.entity.InvoiceItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
 
 // Find all items for an invoice
 List<InvoiceItem> findByInvoice(Invoice invoice);
 
 // Find all items for an invoice by invoice ID
 List<InvoiceItem> findByInvoiceId(Long invoiceId);
 
 // Find items by category for an invoice
 List<InvoiceItem> findByInvoiceAndCategory(Invoice invoice, String category);
 
 // Find items by selection ID (to avoid duplicates)
 Optional<InvoiceItem> findBySelectionId(Long selectionId);
 
 // Get total add-on amount for an invoice
 @Query("SELECT COALESCE(SUM(ii.totalPrice), 0) FROM InvoiceItem ii WHERE ii.invoice = :invoice AND ii.unitPrice > 0")
 BigDecimal getTotalAddonAmountForInvoice(@Param("invoice") Invoice invoice);
 
 // Get base items count (price = 0)
 @Query("SELECT COUNT(ii) FROM InvoiceItem ii WHERE ii.invoice = :invoice AND ii.unitPrice = 0")
 Long getBaseItemsCount(@Param("invoice") Invoice invoice);
 
 // Get add-on items count (price > 0)
 @Query("SELECT COUNT(ii) FROM InvoiceItem ii WHERE ii.invoice = :invoice AND ii.unitPrice > 0")
 Long getAddonItemsCount(@Param("invoice") Invoice invoice);
 
 // Delete all items for an invoice
 @Modifying
 @Transactional
 @Query("DELETE FROM InvoiceItem ii WHERE ii.invoice = :invoice")
 void deleteByInvoice(@Param("invoice") Invoice invoice);
 
 // Get items grouped by category with totals
 @Query("SELECT ii.category, COUNT(ii), SUM(ii.totalPrice) FROM InvoiceItem ii " +
        "WHERE ii.invoice = :invoice GROUP BY ii.category")
 List<Object[]> getCategorySummary(@Param("invoice") Invoice invoice);
 
 // Find items by verification case selection
 @Query("SELECT ii FROM InvoiceItem ii WHERE ii.selectionId IN :selectionIds")
 List<InvoiceItem> findBySelectionIds(@Param("selectionIds") List<Long> selectionIds);
}