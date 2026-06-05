package com.org.bgv.vendor.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.enums.VerificationMethodCode;
import com.org.bgv.vendor.entity.VerificationMethod;


public interface VerificationMethodRepository
        extends JpaRepository<VerificationMethod, Long> {

    Optional<VerificationMethod> findByCode(VerificationMethodCode code);
}
