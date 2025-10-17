package com.org.bgv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.VendorCheckMapping;

@Repository
public interface VendorCheckMappingRepository extends JpaRepository<VendorCheckMapping, Long>{

}
