package com.org.bgv.vendor.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.vendor.entity.VerificationMethodExecutionField;

@Repository
public interface VerificationMethodExecutionFieldRepository
        extends JpaRepository<VerificationMethodExecutionField, Long> {

    List<VerificationMethodExecutionField>
        findByExecutionExecutionId(Long executionId);
    
    Optional<VerificationMethodExecutionField>
    findByExecutionExecutionIdAndFieldName(
            Long executionId,
            String fieldName
    );
}
