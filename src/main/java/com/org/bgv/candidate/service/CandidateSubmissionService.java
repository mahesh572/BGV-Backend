package com.org.bgv.candidate.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.bgv.bgvpackage.entity.EmployerCheckPricing;
import com.org.bgv.bgvpackage.repository.EmployerCheckPricingRepository;
import com.org.bgv.candidate.dto.CandidateVerificationDTO;
import com.org.bgv.candidate.entity.CandidatePackageRule;
import com.org.bgv.candidate.entity.CandidatePackageRuleDocument;
import com.org.bgv.candidate.entity.CandidateVerification;
import com.org.bgv.candidate.repository.CandidatePackageRuleDocumentRepository;
import com.org.bgv.candidate.repository.CandidatePackageRuleRepository;
import com.org.bgv.candidate.repository.CandidateVerificationRepository;
import com.org.bgv.common.DocumentStatus;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.constants.VerificationStatus;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.enums.PricingType;
import com.org.bgv.repository.VerificationCaseCheckRepository;
import com.org.bgv.repository.VerificationCaseDocumentRepository;
import com.org.bgv.repository.VerificationCaseRepository;
import com.org.bgv.service.DocumentService;
import com.org.bgv.service.EducationService;
import com.org.bgv.service.IdentityProofService;
import com.org.bgv.service.ProfileAddressService;
import com.org.bgv.service.ProfileService;
import com.org.bgv.service.VerificationCaseSelectionService;
import com.org.bgv.service.VerificationCaseService;
import com.org.bgv.service.WorkExperienceService;
import com.org.bgv.vendor.dto.ActionStatus;
import com.org.bgv.vendor.entity.VerificationAction;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateSubmissionService {

    private final CandidatePackageRuleRepository ruleRepo;
    private final CandidatePackageRuleDocumentRepository ruleDocRepo;
    private final VerificationCaseCheckRepository caseCheckRepo;
    private final VerificationCaseDocumentRepository caseDocRepo;
    private final CandidateVerificationRepository candidateVerificationRepo;
    private final VerificationCaseService verificationCaseService;
    private final VerificationCaseSelectionService verificationCaseSelectionService;
    private final CandidateVerificationRepository candidateVerificationRepository;
   
    private final VerificationCaseRepository verificationCaseRepository;
    private final VerificationCaseCheckRepository verificationCaseCheckRepository;
    private final VerificationCaseDocumentRepository verificationCaseDocumentRepository;
    private final EmployerCheckPricingRepository employerCheckPricingRepository;
   

    @Transactional
    public CandidateVerificationDTO submitVerification(Long candidateId, Long caseId) {
    	
    	log.info("Submitting verification for candidate: {}, case: {}", candidateId, caseId);

        CandidateVerification candidateverification =
        		candidateVerificationRepository.findByCandidateIdAndVerificationCaseCaseId(candidateId,caseId)
                        .orElseThrow(() -> new EntityNotFoundException("Verification not found"));
        
        

        VerificationCase verificationCase = verificationCaseRepository
                .findByCaseIdAndCandidateId(caseId, candidateId)
                .orElseThrow(() -> new RuntimeException(
                    String.format("Verification case %d not found for candidate %d", caseId, candidateId)
                ));

        // 🔐 Ownership validation
        if (!verificationCase.getCandidateId().equals(candidateId)) {
            throw new ValidationException("Candidate does not own this case");
        }

        // ✅ Progress validation
        
        /*
        int progress = calculateProgress(candidateverification);
        if (progress < 100) {
            throw new ValidationException(
                    "Cannot submit verification. Complete all required sections. Progress: " + progress + "%"
            );
        }
*/
        // -----------------------------
        // 1️⃣ Candidate Verification
        // -----------------------------
        candidateverification.setStatus(VerificationStatus.SUBMITTED);
        candidateverification.setSubmittedAt(LocalDateTime.now());
        candidateverification.setUpdatedAt(LocalDateTime.now());
        candidateVerificationRepository.save(candidateverification);
        // -----------------------------
        // 2️⃣ Verification Case
        // -----------------------------
        verificationCase.setStatus(CaseStatus.SUBMITTED);
        verificationCase.setUpdatedAt(LocalDateTime.now());
        verificationCaseRepository.save(verificationCase);

        // -----------------------------
        // 3️⃣ Checks + Documents
        // -----------------------------
        verificationCase.getCaseChecks().forEach(check -> {

            // Candidate side submit → vendor pending
            if (check.getStatus() == CaseCheckStatus.AWAITING_CANDIDATE
                    || check.getStatus() == CaseCheckStatus.INSUFFICIENT
                    || check.getStatus() == CaseCheckStatus.PENDING_CANDIDATE
                    || check.getStatus() == CaseCheckStatus.ACTION_REQUIRED) 
            {

                check.setStatus(CaseCheckStatus.PENDING);
                check.setUpdatedAt(LocalDateTime.now());
                
                VerificationAction verificationAction = check.getLastAction();
                if(verificationAction!=null) {
                	verificationAction.setStatus(ActionStatus.RESOLVED);
                    check.setLastAction(verificationAction);
                }
                
            }
            verificationCaseCheckRepository.save(check);

         //   List<VerificationCaseDocument> findByVerificationCase_CaseIdAndVerificationCaseCheck_CaseCheckId(caseId,check.get);
            
            // Documents
            check.getDocuments().forEach(document -> {
                if (document.getVerificationStatus() == DocumentStatus.UPLOADED
                        || document.getVerificationStatus() == DocumentStatus.IN_PROGRESS
                        || document.getVerificationStatus() == DocumentStatus.INSUFFICIENT
                        || document.getVerificationStatus() == DocumentStatus.NONE
                        || document.getVerificationStatus() == DocumentStatus.ACTION_REQUIRED
                		) {

                    document.setVerificationStatus(DocumentStatus.PENDING);
                    document.setUpdatedAt(LocalDateTime.now());
                    verificationCaseDocumentRepository.save(document);
                }
            });
           
        });
        
              
       // before submitting check any pending from candidate like action required, 
        // get all documents irrespective of category update the status to Submitted from upload , re upload && active!=false && status!=verified

        // -----------------------------
        // 4️⃣ Persist (cascade)
        // -----------------------------
        
       

        // -----------------------------
        // 5️⃣ Notify vendor / system
        // -----------------------------
       

        
    	verificationCaseSelectionService.populateSelections(caseId);
    	calculatePricing(candidateId, caseId, verificationCase);
    	
    	 return convertToDTO(candidateverification);
    	
    }
    
    private CandidateVerificationDTO convertToDTO(CandidateVerification verification) {
        CandidateVerificationDTO dto = new CandidateVerificationDTO();
        dto.setId(verification.getId());
        dto.setCandidateId(verification.getCandidateId());
     //   dto.setPackageId(verification.getPackageId());
     //   dto.setPackageName(verification.getPackageName());
     //   dto.setEmployerName(verification.getEmployerName());
     //   dto.setEmployerId(verification.getEmployerId());
     //   dto.setDueDate(verification.getDueDate());
        dto.setStartDate(verification.getStartDate());
        dto.setStatus(verification.getStatus());
        dto.setProgressPercentage(verification.getProgressPercentage());
        dto.setInstructions(verification.getInstructions());
        dto.setSupportEmail(verification.getSupportEmail());
        dto.setSubmittedAt(verification.getSubmittedAt());
        dto.setCompletedAt(verification.getCompletedAt());
        dto.setVerificationNotes(verification.getVerificationNotes());
        return dto;
    }
    
    private void calculatePricing(Long candidateId, Long caseId, VerificationCase verificationCase) {

        List<CandidatePackageRule> rules =
                ruleRepo.findByVerificationCase_CaseId(caseId);

        for (CandidatePackageRule rule : rules) {

            // 🔹 Fetch employer pricing
            EmployerCheckPricing pricing =
                    employerCheckPricingRepository
                            .findByCompany_IdAndCheckCategory_CategoryIdAndRuleType_RuleTypeIdAndActiveTrue(
                                    rule.getCompanyId(),
                                    rule.getCheckCategoryId(),
                                    rule.getRuleTypeId()
                            )
                            .orElse(null);
            

            if (pricing == null) {
                log.warn("No pricing found for ruleType {}", rule.getRuleTypeId());
                continue;
            }

            BigDecimal total = BigDecimal.ZERO;

            if (pricing.getPricingType() == PricingType.PER_RECORD) {
                int count = rule.getSelectedCount() != null ? rule.getSelectedCount() : 1;

                total = pricing.getUnitPrice()
                        .multiply(BigDecimal.valueOf(count));
            } else {
                total = pricing.getUnitPrice();
            }

            rule.setUnitPrice(pricing.getUnitPrice());
            rule.setTotalPrice(total);
        }

        ruleRepo.saveAll(rules);
    }
    
}
