package com.org.bgv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.Vendor;
import com.org.bgv.entity.VendorCheckMapping;
import com.org.bgv.onboarding.entity.Company;

@Repository
public interface VendorCheckMappingRepository
        extends JpaRepository<VendorCheckMapping, Long> {

    @Query("""
        SELECT vcm.vendorCompany
        FROM VendorCheckMapping vcm
        WHERE vcm.category.categoryId = :categoryId
          AND vcm.isActive = true
          AND vcm.vendorCompany.status = 'ACTIVE'
    """)
    List<Company> findActiveVendorCompaniesByCategory(
            @Param("categoryId") Long categoryId);

    boolean existsByVendorCompanyAndCategoryAndIsActiveTrue(
            Company vendorCompany,
            CheckCategory category);
    
    @Query("""
    		SELECT vcm.vendorCompany
    		FROM VendorCheckMapping vcm
    		WHERE
    		    vcm.category.categoryId=:categoryId
    		    AND vcm.isActive=true
    		""")
    		List<Company> findCompaniesByCategory(
    		        @Param("categoryId") Long categoryId);

}
