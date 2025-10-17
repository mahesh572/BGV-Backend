package com.org.bgv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.Vendor;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    
  //  Optional<Vendor> findByEmail(String email);
    
    List<Vendor> findByStatus(String status);
    
    List<Vendor> findByVendorType(String vendorType);
    
   // List<Vendor> findBySpecializationContaining(String specialization);
    
    List<Vendor> findByCity(String city);
    
    List<Vendor> findByCountry(String country);
    
	/*
	 * @Query("SELECT v FROM Vendor v WHERE v.firstName LIKE %:name% OR v.lastName LIKE %:name%"
	 * ) List<Vendor> findByNameContaining(@Param("name") String name);
	 */
    
    @Query("SELECT v FROM Vendor v WHERE v.businessName LIKE %:businessName%")
    List<Vendor> findByBusinessNameContaining(@Param("businessName") String businessName);
    
   
    
    @Query("SELECT v FROM Vendor v WHERE v.experience >= :minExperience")
    List<Vendor> findByMinExperience(@Param("minExperience") Integer minExperience);
    
  //  boolean existsByEmail(String email);
    
    boolean existsByRegistrationNumber(String registrationNumber);
    
    boolean existsByTaxId(String taxId);
}