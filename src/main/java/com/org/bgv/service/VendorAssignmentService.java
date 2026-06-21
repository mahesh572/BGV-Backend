package com.org.bgv.service;

import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.User;
import com.org.bgv.entity.Vendor;
import com.org.bgv.repository.VerificationCaseCheckRepository;
import com.org.bgv.vendor.repository.VendorUserCategoryMappingRepository;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.CompanyUserRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.repository.VendorCheckMappingRepository;
import com.org.bgv.repository.VendorRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendorAssignmentService {

    private final VendorCheckMappingRepository vendorCheckMappingRepository;
    private final VendorRepository vendorRepository;
    private final VerificationCaseCheckRepository verificationCaseCheckRepository;
    private final CheckCategoryRepository checkCategoryRepository;
    private final UserRepository userRepository;
    private final CompanyUserRepository companyUserRepository;
    private final VendorUserCategoryMappingRepository vendorUserCategoryMappingRepository;

    /**
     * AUTO ASSIGN vendor based on category
     */
   /*
    public Vendor autoAssignVendor(Long categoryId) {

        List<Vendor> eligibleVendors =
                vendorCheckMappingRepository.findActiveVendorsByCategory(categoryId);

        if (eligibleVendors.isEmpty()) {
            throw new RuntimeException(
                "No active vendors available for categoryId=" + categoryId
            );
        }

        // 🔹 Strategy: Least active checks (scalable)
        Vendor selectedVendor = eligibleVendors.stream()
            .min(Comparator.comparing(this::getActiveCheckCount))
            .orElseThrow();

        log.info("Auto-assigned vendor {} for category {}", 
                 selectedVendor.getId(), categoryId);

        return selectedVendor;
    }
    
    */

    /**
     * MANUAL ASSIGN vendor to a case check
     */
    
    /*
    @Transactional
    public void assignVendorToCheck(Long caseCheckId, Long vendorId) {

        VerificationCaseCheck caseCheck =
                verificationCaseCheckRepository.findById(caseCheckId)
                .orElseThrow(() ->
                    new RuntimeException("VerificationCaseCheck not found")
                );

        validateVendorSupportsCategory(vendorId, caseCheck.getCategory().getCategoryId());

        caseCheck.setVendorUserId(vendorId);
      //  caseCheck.setStatus(CaseStatus.ASSIGNED);

        verificationCaseCheckRepository.save(caseCheck);

        log.info("Vendor {} assigned to check {}", vendorId, caseCheckId);
    }
    */

    /**
     * REASSIGN vendor (admin / failure case)
     */
   /*
    @Transactional
    public void reassignVendor(Long caseCheckId, Long newVendorId) {

        VerificationCaseCheck caseCheck =
                verificationCaseCheckRepository.findById(caseCheckId)
                .orElseThrow(() ->
                    new RuntimeException("VerificationCaseCheck not found")
                );

        validateVendorSupportsCategory(newVendorId,
                caseCheck.getCategory().getCategoryId());

        caseCheck.setVendorUserId(newVendorId);
       // caseCheck.setStatus(CaseStatus.REASSIGNED);

        verificationCaseCheckRepository.save(caseCheck);

        log.info("Vendor reassigned to {} for check {}", 
                 newVendorId, caseCheckId);
    }
    
    */

    /**
     * GET assigned vendor
     */
    
    /*
   
    public Vendor getAssignedVendor(Long caseCheckId) {

        VerificationCaseCheck check =
                verificationCaseCheckRepository.findById(caseCheckId)
                .orElseThrow(() ->
                    new RuntimeException("VerificationCaseCheck not found")
                );

        if (check.getVendorUserId() == null) return null;

        return vendorRepository.findById(check.getVendorUserId())
                .orElse(null);
    }

    // ===============================
    // INTERNAL HELPERS
    // ===============================

    private void validateVendorSupportsCategory(Long vendorId, Long categoryId) {

        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new RuntimeException("Vendor not found: " + vendorId));

        CheckCategory category = checkCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));

        boolean supported =
            vendorCheckMappingRepository
                .existsByVendorAndCategoryAndIsActiveTrue(vendor, category);

        if (!supported) {
            throw new RuntimeException(
                "Vendor " + vendorId + " does not support category " + categoryId
            );
        }
    }
*/

    /**
     * Used for load balancing (future ready)
     */
    /*
    private long getActiveCheckCount(Vendor vendor) {
        return verificationCaseCheckRepository
                .countByVendorIdAndStatusIn(
                        vendor.getId(),
                        List.of(
                            CaseCheckStatus.AWAITING_CANDIDATE,
                            CaseCheckStatus.PENDING
                        )
                );
    }
    
    */
    /*
    @Transactional   cannot be used
    public boolean assignVendorsToCaseChecks(
            List<VerificationCaseCheck> checks) {

        boolean allAssigned = true;

        for (VerificationCaseCheck check : checks) {

            Vendor vendor =
                    autoAssignVendor(check.getCategory().getCategoryId());

            if (vendor == null) {

                check.setStatus(
                        CaseCheckStatus.PENDING_VENDOR_ASSIGNMENT);

                allAssigned = false;
                continue;
            }

            check.setVendorUserId(vendor.getId());
            check.setStatus(CaseCheckStatus.ASSIGNED);
        }

        verificationCaseCheckRepository.saveAll(checks);

        return allAssigned;
    }
    */
    /*
    @Transactional
    public boolean assignVendorsToCaseChecks(
            List<VerificationCaseCheck> checks) {

        boolean allAssigned = true;

        for (VerificationCaseCheck check : checks) {

            Vendor vendor =
                    autoAssignVendor(
                            check.getCategory().getCategoryId());

            if (vendor == null) {

                check.setStatus(
                        CaseCheckStatus.PENDING_VENDOR_ASSIGNMENT);

                allAssigned = false;
                continue;
            }

            // assign company
            check.setVendorCompany(vendor.getCompany());

            // waiting for vendor admin to assign agent
            check.setStatus(
                    CaseCheckStatus.PENDING_AGENT_ASSIGNMENT);
        }

        verificationCaseCheckRepository.saveAll(checks);

        return allAssigned;
    }
    
    @Transactional
    public void assignVendorUser(
            Long caseCheckId,
            Long vendorUserId,
            Long assignedByUserId) {

        VerificationCaseCheck check =
                verificationCaseCheckRepository.findById(caseCheckId)
                .orElseThrow(() ->
                        new RuntimeException("Check not found"));

        User vendorUser =
                userRepository.findById(vendorUserId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor user not found"));

        User assignedBy =
                userRepository.findById(assignedByUserId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // safety check
        validateVendorUser(
                check.getVendorCompany().getId(),
                vendorUserId);

        check.setAssignedVendorUser(vendorUser);
        check.setAssignedBy(assignedBy);
        check.setAssignedAt(LocalDateTime.now());

        check.setStatus(CaseCheckStatus.ASSIGNED);

        verificationCaseCheckRepository.save(check);
    }
    
    private void validateVendorUser(
            Long companyId,
            Long userId) {

        boolean exists =
                companyUserRepository
                        .existsByCompanyIdAndUserId(
                                companyId,
                                userId);

        if (!exists) {

            throw new RuntimeException(
                    "User does not belong to vendor company");
        }
    }
    */
    
    @Transactional    // Vendor admin assigns agent
    public void assignVendorUser(
            Long caseCheckId,
            Long vendorUserId,
            Long assignedByUserId) {

        VerificationCaseCheck check =
                verificationCaseCheckRepository
                        .findById(caseCheckId)
                        .orElseThrow(() ->
                                new RuntimeException("Check not found"));

        User vendorUser =
                userRepository.findById(vendorUserId)
                        .orElseThrow(() ->
                                new RuntimeException("Vendor user not found"));

        User assignedBy =
                userRepository.findById(assignedByUserId)
                        .orElseThrow();

        // user must belong to same vendor company
        validateVendorUser(
                check.getVendorCompany().getId(),
                vendorUserId);

        // user must support this category
        validateVendorUserCategory(
                vendorUserId,
                check.getCategory().getCategoryId());

        check.setAssignedVendorUser(vendorUser);
        check.setAssignedBy(assignedBy);
        check.setAssignedAt(LocalDateTime.now());

        check.setStatus(CaseCheckStatus.ASSIGNED);

        verificationCaseCheckRepository.save(check);
    }
    
    @Transactional
    public boolean assignVendorsToCaseChecks(
            List<VerificationCaseCheck> checks) {

        boolean allAssigned = true;

        for (VerificationCaseCheck check : checks) {

            Company vendorCompany =
                    autoAssignVendorCompany(
                            check.getCategory().getCategoryId());

            if (vendorCompany == null) {

                check.setStatus(
                        CaseCheckStatus.PENDING_VENDOR_ASSIGNMENT);

                allAssigned = false;
                continue;
            }

            check.setVendorCompany(vendorCompany);

            // waiting for vendor admin
            check.setStatus(
                    CaseCheckStatus.PENDING_AGENT_ASSIGNMENT);
        }

        verificationCaseCheckRepository.saveAll(checks);

        return allAssigned;
    }
    
    private Company autoAssignVendorCompany(
            Long categoryId) {

        List<Company> companies =
                vendorCheckMappingRepository
                        .findCompaniesByCategory(categoryId);

        if (companies.isEmpty()) {
            return null;
        }

        return companies.get(0);
    }
    
    private void validateVendorUser(
            Long companyId,
            Long userId) {

        boolean exists =
                companyUserRepository
                        .existsByCompanyIdAndUserId(
                                companyId,
                                userId);

        if (!exists) {
            throw new RuntimeException(
                    "User does not belong to vendor company");
        }
    }
    
    private void validateVendorUserCategory(
            Long userId,
            Long categoryId) {

        boolean supported =
                vendorUserCategoryMappingRepository
                        .existsByVendorUser_UserIdAndCategory_CategoryIdAndActiveTrue(
                                userId,
                                categoryId);

        if (!supported) {
            throw new RuntimeException(
                    "Vendor user does not support this category");
        }
    }
    
}
