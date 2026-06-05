package com.org.bgv.vendor.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.enums.VerificationExecutionStatus;
import com.org.bgv.vendor.entity.VerificationMethodExecution;

@Repository
public interface VerificationMethodExecutionRepository
        extends JpaRepository<VerificationMethodExecution, Long> {

    List<VerificationMethodExecution>
        findByVerificationCheckCaseCheckId(Long checkId);
    
    List<VerificationMethodExecution>
    findByVerificationCheckCaseCheckIdAndObjectIdOrderByInitiatedAtDesc(
            Long checkId,
            Long objectId
    );
    
    
    boolean existsByVerificationCheckCaseCheckIdAndObjectIdAndVerificationMethodMethodIdAndStatusIn(
            Long checkId,
            Long objectId,
            Long methodId,
            List<VerificationExecutionStatus> statuses);
    
    boolean existsByVerificationCheckCaseCheckIdAndObjectIdAndVerificationMethodMethodIdAndStatusNotIn(
            Long checkId,
            Long objectId,
            Long methodId,
            List<VerificationExecutionStatus> statuses);

}
