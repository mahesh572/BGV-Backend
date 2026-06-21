package com.org.bgv.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.User;
import com.org.bgv.vendor.entity.VendorUserCategoryMapping;

public interface VendorUserCategoryMappingRepository extends JpaRepository<VendorUserCategoryMapping, Long> {

    List<VendorUserCategoryMapping>  findByCategoryCategoryIdAndActiveTrue(Long categoryId);
    
    boolean existsByVendorUserAndCategory(User vendorUser, CheckCategory category);
    
    boolean existsByVendorUser_UserIdAndCategory_CategoryIdAndActiveTrue(Long vendorUser_UserId, Long category_CategoryId);

}
