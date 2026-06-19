package com.org.bgv.vendor.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.vendor.entity.VerificationFieldComparison;
import com.org.bgv.vendor.entity.VerificationObject;

public interface VerificationFieldComparisonRepository
        extends JpaRepository<VerificationFieldComparison, Long> {
	
	boolean existsByVerificationObject(VerificationObject object);

    List<VerificationFieldComparison>
            findByVerificationObject(VerificationObject object);
    
    List<VerificationFieldComparison>
    findByVerificationObjectOrderById(
            VerificationObject object);

}
