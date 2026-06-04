package com.org.bgv.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.vendor.entity.VerificationMethodField;

@Repository
public interface VerificationMethodFieldRepository
        extends JpaRepository<VerificationMethodField, Long> {
	
	List<VerificationMethodField>
    findByVerificationMethodMethodIdOrderByDisplayOrderAsc(
            Long methodId);
	
	
}