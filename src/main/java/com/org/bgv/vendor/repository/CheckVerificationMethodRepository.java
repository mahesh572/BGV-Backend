package com.org.bgv.vendor.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.vendor.entity.CheckVerificationMethod;


public interface CheckVerificationMethodRepository
        extends JpaRepository<CheckVerificationMethod, Long> {

    List<CheckVerificationMethod> findByCheckType(
            CheckCategoryEnum checkType
    );
}
