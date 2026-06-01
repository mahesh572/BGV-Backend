package com.org.bgv.invoice.repository;


import com.org.bgv.constants.PaymentStatus;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.enums.PaymentMethod;
import com.org.bgv.invoice.entity.CasePayment;
import com.org.bgv.invoice.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CasePaymentRepository extends JpaRepository<CasePayment, Long> {

	List<CasePayment> findByVerificationCase(VerificationCase verificationCase);
	
	@Transactional
    void deleteByVerificationCase_CaseId(Long caseId);

    @Transactional
    void deleteByInvoice_Id(Long invoiceId);

    @Transactional
    void deleteByWalletTransaction_TransactionId(Long transactionId);
}