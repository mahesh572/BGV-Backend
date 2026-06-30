package com.org.bgv.vendor.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.vendor.entity.CheckVerificationMethod;
import com.org.bgv.vendor.entity.VerificationMethod;


public interface CheckVerificationMethodRepository
        extends JpaRepository<CheckVerificationMethod, Long> {

    List<CheckVerificationMethod> findByCheckType(
            CheckCategoryEnum checkType
    );
    
    boolean existsByCheckTypeAndVerificationMethod(
            CheckCategoryEnum checkType,
            VerificationMethod method);
}
