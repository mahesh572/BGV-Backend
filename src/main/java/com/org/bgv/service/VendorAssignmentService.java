package com.org.bgv.service;

import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.Vendor;
import com.org.bgv.repository.VerificationCaseCheckRepository;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.VendorCheckMappingRepository;
import com.org.bgv.repository.VendorRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

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

    /**
     * AUTO ASSIGN vendor based on category
     */
   
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

    /**
     * MANUAL ASSIGN vendor to a case check
     */
    
    @Transactional
    public void assignVendorToCheck(Long caseCheckId, Long vendorId) {

        VerificationCaseCheck caseCheck =
                verificationCaseCheckRepository.findById(caseCheckId)
                .orElseThrow(() ->
                    new RuntimeException("VerificationCaseCheck not found")
                );

        validateVendorSupportsCategory(vendorId, caseCheck.getCategory().getCategoryId());

        caseCheck.setVendorId(vendorId);
      //  caseCheck.setStatus(CaseStatus.ASSIGNED);

        verificationCaseCheckRepository.save(caseCheck);

        log.info("Vendor {} assigned to check {}", vendorId, caseCheckId);
    }

    /**
     * REASSIGN vendor (admin / failure case)
     */
   
    @Transactional
    public void reassignVendor(Long caseCheckId, Long newVendorId) {

        VerificationCaseCheck caseCheck =
                verificationCaseCheckRepository.findById(caseCheckId)
                .orElseThrow(() ->
                    new RuntimeException("VerificationCaseCheck not found")
                );

        validateVendorSupportsCategory(newVendorId,
                caseCheck.getCategory().getCategoryId());

        caseCheck.setVendorId(newVendorId);
       // caseCheck.setStatus(CaseStatus.REASSIGNED);

        verificationCaseCheckRepository.save(caseCheck);

        log.info("Vendor reassigned to {} for check {}", 
                 newVendorId, caseCheckId);
    }

    /**
     * GET assigned vendor
     */
   
    public Vendor getAssignedVendor(Long caseCheckId) {

        VerificationCaseCheck check =
                verificationCaseCheckRepository.findById(caseCheckId)
                .orElseThrow(() ->
                    new RuntimeException("VerificationCaseCheck not found")
                );

        if (check.getVendorId() == null) return null;

        return vendorRepository.findById(check.getVendorId())
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


    /**
     * Used for load balancing (future ready)
     */
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
    
    @Transactional
    public void assignVendorsToCaseChecks(List<VerificationCaseCheck> checks) {
        for (VerificationCaseCheck check : checks) {
            Vendor vendor = autoAssignVendor(check.getCategory().getCategoryId());
            check.setVendorId(vendor.getId());
           // check.setStatus(CaseStatus.ASSIGNED);
        }
    }
    
    
}
