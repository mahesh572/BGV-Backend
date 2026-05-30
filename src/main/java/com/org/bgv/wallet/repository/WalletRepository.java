package com.org.bgv.wallet.repository;

import com.org.bgv.entity.Wallet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    
    Optional<Wallet> findByUserId(Long userId);
    
    Optional<Wallet> findByUserIdAndCompanyId(Long userId, Long companyId);
    
    Optional<Wallet> findByCompanyId(Long companyId);
    
    @Query("SELECT w FROM Wallet w WHERE w.userId = :userId AND w.companyId = :companyId AND w.isActive = true")
    Optional<Wallet> findActiveWallet(@Param("userId") Long userId, @Param("companyId") Long companyId);
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM WalletTransaction t WHERE t.wallet.userId = :userId AND t.status = 'SUCCESS' AND t.transactionType = 'CREDIT'")
    BigDecimal getTotalCredits(@Param("userId") Long userId);
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM WalletTransaction t WHERE t.wallet.userId = :userId AND t.status = 'SUCCESS' AND t.transactionType = 'DEBIT'")
    BigDecimal getTotalDebits(@Param("userId") Long userId);
}