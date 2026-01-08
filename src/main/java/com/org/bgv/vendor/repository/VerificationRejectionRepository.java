package com.org.bgv.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.vendor.dto.RejectionStatus;
import com.org.bgv.vendor.entity.VerificationRejection;
import java.util.List;


@Repository
public interface VerificationRejectionRepository extends JpaRepository<VerificationRejection, Long>{
	
	
	List<VerificationRejection> findByVerificationCaseCheck_CaseCheckId(Long verificationCaseCheck_CaseCheckId);

	boolean existsByVerificationCaseCheckAndStatus(
            VerificationCaseCheck verificationCaseCheck,
            RejectionStatus status
    );
}
