package com.org.bgv.wallet.repository;

import com.org.bgv.entity.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    
    Optional<WalletTransaction> findByTransactionRef(String transactionRef);
    
    Optional<WalletTransaction> findByRazorpayPaymentId(String razorpayPaymentId);
    
    Page<WalletTransaction> findByWalletCompanyId(Long companyId, Pageable pageable);
    
    Page<WalletTransaction> findByWalletUserIdAndTransactionType(Long userId, String transactionType, Pageable pageable);
    
    List<WalletTransaction> findByWalletUserIdAndCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM WalletTransaction t WHERE t.wallet.userId = :userId AND t.status = :status ORDER BY t.createdAt DESC")
    List<WalletTransaction> findRecentTransactions(@Param("userId") Long userId, @Param("status") String status, Pageable pageable);
    
    @Query("SELECT wt FROM WalletTransaction wt " +
            "WHERE wt.wallet.id = :walletId")
     Page<WalletTransaction> findByWalletId(@Param("walletId") Long walletId, Pageable pageable);
    
 // ADD THESE METHODS for duplicate prevention
    boolean existsByRazorpayPaymentId(String razorpayPaymentId);
    
    boolean existsByRazorpayOrderId(String razorpayOrderId);
    
    boolean existsByRazorpayPaymentIdOrRazorpayOrderId(String razorpayPaymentId, String razorpayOrderId);
    
}