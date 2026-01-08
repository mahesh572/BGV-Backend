package com.org.bgv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.Vendor;
import com.org.bgv.entity.VendorCheckMapping;

@Repository
public interface VendorCheckMappingRepository extends JpaRepository<VendorCheckMapping, Long>{
	
	
	@Query("""
	        SELECT vcm.vendor
	        FROM VendorCheckMapping vcm
	        WHERE vcm.category.id = :categoryId
	          AND vcm.isActive = true
	          AND vcm.vendor.status = 'ACTIVE'
	    """)
	    List<Vendor> findActiveVendorsByCategory(@Param("categoryId") Long categoryId);

	boolean existsByVendorAndCategoryAndIsActiveTrue(
		    Vendor vendor,
		    CheckCategory category
		);
}
