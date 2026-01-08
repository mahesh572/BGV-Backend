package com.org.bgv.candidate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.Address;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    // Find all addresses for a candidate
    List<Address> findByCandidateId(Long candidateId);
    
    // Find addresses by candidate ID and status
    List<Address> findByCandidateIdAndStatus(Long candidateId, String status);
    
    // Find current address (where currently residing)
    Optional<Address> findByCandidateIdAndCurrentlyResidingAtThisAddress(Long candidateId, boolean currentlyResidingAtThisAddress);
    
    // Find permanent address
    Optional<Address> findByCandidateIdAndIsMyPermanentAddress(Long candidateId, boolean isMyPermanentAddress);
    
    // Find addresses by verification status
    List<Address> findByCandidateIdAndVerificationStatus(Long candidateId, String verificationStatus);
    
    // Find verified addresses
    List<Address> findByCandidateIdAndVerified(Long candidateId, boolean verified);
    
    // Find addresses by type
    List<Address> findByCandidateIdAndAddressType(Long candidateId, String addressType);
    
    // Count addresses by candidate
    @Query("SELECT COUNT(a) FROM Address a WHERE a.candidateId = :candidateId AND a.status = 'active'")
    Long countActiveAddressesByCandidateId(@Param("candidateId") Long candidateId);
    
    // Find addresses that need verification (pending)
    @Query("SELECT a FROM Address a WHERE a.candidateId = :candidateId AND a.verificationStatus = 'pending' AND a.status = 'active'")
    List<Address> findPendingVerificationAddresses(@Param("candidateId") Long candidateId);
    
    // Find primary addresses (current or permanent)
    @Query("SELECT a FROM Address a WHERE a.candidateId = :candidateId AND " +
           "(a.currentlyResidingAtThisAddress = true OR a.isMyPermanentAddress = true) AND " +
           "a.status = 'active'")
    List<Address> findPrimaryAddresses(@Param("candidateId") Long candidateId);
    
    // Check if address exists for candidate
    boolean existsByCandidateIdAndAddressLine1AndCityAndZipCode(
        @Param("candidateId") Long candidateId,
        @Param("addressLine1") String addressLine1,
        @Param("city") String city,
        @Param("zipCode") String zipCode
    );
    
    // Find addresses within a date range
    @Query("SELECT a FROM Address a WHERE a.candidateId = :candidateId AND " +
           "a.currentlyResidingFrom BETWEEN :startDate AND :endDate AND " +
           "a.status = 'active'")
    List<Address> findAddressesByResidingDateRange(
        @Param("candidateId") Long candidateId,
        @Param("startDate") java.time.LocalDate startDate,
        @Param("endDate") java.time.LocalDate endDate
    );
    
    // Find addresses by city
    List<Address> findByCityIgnoreCase(String city);
    
    // Find addresses by state
    List<Address> findByStateIgnoreCase(String state);
    
    // Find addresses by country
    List<Address> findByCountryIgnoreCase(String country);
    
    // Find addresses with coordinates
    @Query("SELECT a FROM Address a WHERE a.latitude IS NOT NULL AND a.longitude IS NOT NULL")
    List<Address> findAddressesWithCoordinates();
    
    // Find addresses needing validation
    @Query("SELECT a FROM Address a WHERE a.isValidated = false AND a.status = 'active'")
    List<Address> findAddressesNeedingValidation();
}