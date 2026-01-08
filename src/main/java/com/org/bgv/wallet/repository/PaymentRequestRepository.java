package com.org.bgv.wallet.repository;

import com.org.bgv.entity.PaymentRequest;
import com.org.bgv.constants.PaymentStatus;
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
public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, Long> {
    
    Optional<PaymentRequest> findByRequestRef(String requestRef);
    
    Optional<PaymentRequest> findByRazorpayOrderId(String razorpayOrderId);
    
    Page<PaymentRequest> findByUserId(Long userId, Pageable pageable);
    
    Page<PaymentRequest> findByUserIdAndStatus(Long userId, PaymentStatus status, Pageable pageable);
    
    List<PaymentRequest> findByExpiresAtBeforeAndStatus(LocalDateTime dateTime, PaymentStatus status);
    
    @Query("SELECT p FROM PaymentRequest p WHERE p.userId = :userId AND p.purpose = :purpose AND p.status = 'PAID' ORDER BY p.createdAt DESC")
    List<PaymentRequest> findSuccessfulPaymentsByPurpose(@Param("userId") Long userId, @Param("purpose") String purpose);
}