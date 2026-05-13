package com.org.bgv.candidate.service;



import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.bgv.candidate.dto.CandidateVerificationDTO;
import com.org.bgv.candidate.dto.SectionStatusUpdateRequest;
import com.org.bgv.candidate.dto.VerificationSectionDTO;
import com.org.bgv.candidate.entity.CandidateVerification;
import com.org.bgv.candidate.repository.CandidateVerificationRepository;
import com.org.bgv.common.DocumentStatus;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.constants.SectionConstants;
import com.org.bgv.constants.SectionStatus;
import com.org.bgv.constants.VerificationStatus;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.entity.VerificationCaseDocument;
import com.org.bgv.repository.VerificationCaseCheckRepository;
import com.org.bgv.repository.VerificationCaseDocumentRepository;
import com.org.bgv.repository.VerificationCaseRepository;
import com.org.bgv.service.DocumentService;
import com.org.bgv.service.EducationService;
import com.org.bgv.service.IdentityProofService;
import com.org.bgv.service.ProfileAddressService;
import com.org.bgv.service.ProfileService;
import com.org.bgv.service.WorkExperienceService;
import com.org.bgv.vendor.dto.ActionStatus;
import com.org.bgv.vendor.dto.BaseCheckDTO;
import com.org.bgv.vendor.entity.VerificationAction;

import ch.qos.logback.classic.Logger;
import jakarta.persistence.EntityNotFoundException;
import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationService {
    
    private final CandidateVerificationRepository candidateVerificationRepository;
    private final ObjectMapper objectMapper;
    private final ProfileService profileService;
    private final EducationService educationService;
    private final WorkExperienceService workExperienceService;
    private final IdentityProofService identityService;
    private final ProfileAddressService addressService;
    private final DocumentService documentsService;
    private final VerificationCaseRepository verificationCaseRepository;
    private final VerificationCaseCheckRepository verificationCaseCheckRepository;
    private final VerificationCaseDocumentRepository verificationCaseDocumentRepository;
    private final VerificationHelperService verificationHelperService;
    
   
    
    @Cacheable(value = "verification", key = "#candidateId")
    public CandidateVerificationDTO getCandidateVerification(Long candidateId,Long caseId) {
        log.info("Fetching verification for candidate: {}{}", candidateId,caseId);
        
        CandidateVerification verification = candidateVerificationRepository.findByCandidateIdAndVerificationCaseCaseId(candidateId,caseId)
            .orElseThrow(() -> new EntityNotFoundException("Verification not found for candidate: " + candidateId));
        
        CandidateVerificationDTO dto = convertToDTO(verification);
        
        // Calculate progress
        dto.setProgressPercentage(calculateProgress(verification));
        log.info("before..................getSectionsWithStatus");
        // Get section requirements and status
        Map<String, VerificationSectionDTO> sections = getSectionsWithStatus(candidateId, verification);
        dto.setSections(sections);
        
                
        return dto;
    }
    
    @Transactional
    @CacheEvict(value = "verification", key = "#candidateId")
    public CandidateVerificationDTO updateSectionStatus(SectionStatusUpdateRequest sectionStatusUpdateRequest) throws ValidationException {
        log.info("Updating section {} status to {} for candidate: {}", sectionStatusUpdateRequest.getSection(), sectionStatusUpdateRequest.getStatus(), sectionStatusUpdateRequest.getCandidateId());
        
        CandidateVerification candidateVerification = candidateVerificationRepository.findByCandidateIdAndVerificationCaseCaseId(sectionStatusUpdateRequest.getCandidateId(),sectionStatusUpdateRequest.getCaseId())
            .orElseThrow(() -> new EntityNotFoundException("Verification not found"));
        
        // Update section status in JSON field
        Map<String, Map<String, Object>> sectionStatusMap = verificationHelperService.getSectionStatusMap(candidateVerification);
        Map<String, Object> sectionData = sectionStatusMap.getOrDefault(sectionStatusUpdateRequest.getSection(), new HashMap<>());
        
        
        log.info("sectionData.get(\"status\"):::::sectionData.get(\"status\").equals(VerificationStatus.ACTION_REQUIRED)::{}{}",sectionData.get("status"),sectionData.get("status").equals(VerificationStatus.ACTION_REQUIRED.name()));
        
        if(sectionStatusUpdateRequest.getStatus()!=null && sectionStatusUpdateRequest.getStatus().equalsIgnoreCase(VerificationStatus.SUBMITTED.name()) && sectionData.get("status").equals(VerificationStatus.ACTION_REQUIRED.name())) {
        	
        	
        	Object countObj = sectionData.get("resubmissionCount");

            int resubmissionCount = 0;
            
            if (countObj instanceof Number) {
                resubmissionCount = ((Number) countObj).intValue();
            } else if (countObj instanceof String) {
                try {
                    resubmissionCount = Integer.parseInt((String) countObj);
                } catch (NumberFormatException ignored) {}
            }
            
            resubmissionCount++;
        	
        	sectionData.put("resubmissionCount", resubmissionCount);
        	 sectionData.put("resubmitted", Boolean.TRUE);
        }
        
        sectionData.put("status", sectionStatusUpdateRequest.getStatus().toString());
        sectionData.put("lastUpdated", LocalDateTime.now().toString());
        
        
        sectionStatusMap.put(sectionStatusUpdateRequest.getSection(), sectionData);
        
        try {
        	candidateVerification.setSectionStatus(objectMapper.writeValueAsString(sectionStatusMap));
        	candidateVerification.setUpdatedAt(LocalDateTime.now());
        	candidateVerification = candidateVerificationRepository.save(candidateVerification);
        } catch (Exception e) {
            log.error("Error updating section status: {}", e.getMessage());
            throw new ValidationException("Failed to update section status");
        }
        
        // Update overall progress
      //  int progress = calculateProgress(candidateVerification);
      //  candidateVerification.setProgressPercentage(progress);
        
        // Update verification status if all required sections are completed
        /*
        if (progress == 100 && candidateVerification.getStatus() == VerificationStatus.IN_PROGRESS) {
        	candidateVerification.setStatus(VerificationStatus.SUBMITTED);
        	candidateVerification.setSubmittedAt(LocalDateTime.now());
        }
        */
        if (candidateVerification.getStatus() == VerificationStatus.IN_PROGRESS) {
        	candidateVerification.setStatus(VerificationStatus.SUBMITTED);
        	candidateVerification.setSubmittedAt(LocalDateTime.now());
        }
        
        candidateVerification = candidateVerificationRepository.save(candidateVerification);
        
        
        
        
        return convertToDTO(candidateVerification);
    }
    
    @Transactional
    @CacheEvict(value = "verification", key = "#candidateId")
    public CandidateVerificationDTO submitForVerification(Long candidateId, Long caseId)
            throws ValidationException {

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
        sendVerificationSubmittedNotification(candidateverification);

        return convertToDTO(candidateverification);
    }

    
    @Transactional
    @CacheEvict(value = "verification", key = "#candidateId")
    public CandidateVerificationDTO createCandidateVerification(Long candidateId, CandidateVerificationDTO request) throws ValidationException {
        log.info("Creating verification for candidate: {}", candidateId);
        
        // Check if verification already exists
        if (candidateVerificationRepository.findByCandidateId(candidateId).isPresent()) {
            throw new ValidationException("Verification already exists for this candidate");
        }
        
        CandidateVerification verification = new CandidateVerification();
        verification.setCandidateId(candidateId);
      //  verification.setPackageId(request.getPackageId());
     //   verification.setPackageName(request.getPackageName());
     //   verification.setEmployerName(request.getEmployerName());
     //   verification.setEmployerId(request.getEmployerId());
    //    verification.setDueDate(request.getDueDate());
        verification.setStartDate(LocalDateTime.now());
        verification.setStatus(VerificationStatus.PENDING);
        verification.setProgressPercentage(0);
        verification.setInstructions(request.getInstructions());
        verification.setSupportEmail(request.getSupportEmail());
        
        // Set section requirements based on package
        setSectionRequirements(verification, request.getPackageId());
        
        verification = candidateVerificationRepository.save(verification);
        
        return convertToDTO(verification);
    }
    
    private int calculateProgress(CandidateVerification verification) {
        try {
            Map<String, Map<String, Object>> sectionStatusMap = verificationHelperService.getSectionStatusMap(verification);
            Map<String, Map<String, Object>> requirementsMap = getRequirementsMap(verification);
            
            int totalRequired = 0;
            int completed = 0;
            
            for (Map.Entry<String, Map<String, Object>> entry : requirementsMap.entrySet()) {
                boolean required = (boolean) entry.getValue().getOrDefault("required", false);
                if (required) {
                    totalRequired++;
                    
                    Map<String, Object> statusData = sectionStatusMap.get(entry.getKey());
                    if (statusData != null) {
                       /*
                    	String status = (String) statusData.getOrDefault("status", SectionStatus.NOT_STARTED.toString());
                        if (SectionStatus.COMPLETED.toString().equals(status) || 
                            SectionStatus.VERIFIED.toString().equals(status)) {
                            completed++;
                        }
                        */
                        String statusStr = (String) statusData.get("status");
                        SectionStatus sectionStatus = SectionStatus.fromString(statusStr);
                        
                        log.info("statusStr:::::::::::sectionStatus:::::{}{}",statusStr,sectionStatus);

                        if (sectionStatus == SectionStatus.COMPLETED ||
                            sectionStatus == SectionStatus.VERIFIED) {
                            completed++;
                        }
                    }
                }
            }
            
            log.info("totalRequired:::::completed:::::{}{}",totalRequired,completed);
            
            return totalRequired > 0 ? (completed * 100) / totalRequired : 0;
        } catch (Exception e) {
            log.error("Error calculating progress: {}", e.getMessage());
            return 0;
        }
    }
    
    
    /*
    private Map<String, VerificationSectionDTO> getSectionsWithStatus(Long candidateId, CandidateVerification verification) {
        
    	Map<String, VerificationSectionDTO> sections = new LinkedHashMap();
        
        try {
            Map<String, Map<String, Object>> requirementsMap = getRequirementsMap(verification);
            Map<String, Map<String, Object>> statusMap = verificationHelperService.getSectionStatusMap(verification);
            
            // Basic Details
          //  addSection(sections, SectionConstants.BASIC_DETAILS.getValue(), "Basic Details", requirementsMap, statusMap, 
          //            () -> profileService.getBasicDetails(candidateId));
            // Identity
            addSection(sections, SectionConstants.IDENTITY.getValue(), "Identity", requirementsMap, statusMap, 
                      () -> identityService.getIdentityInfo(candidateId));
            
            // Education
            addSection(sections, SectionConstants.EDUCATION.getValue(), "Education", requirementsMap, statusMap, 
                      () -> educationService.getEducations(candidateId));
            
            // Work Experience
            addSection(sections, SectionConstants.WORK_EXPERIENCE.getValue(), "Work Experience", requirementsMap, statusMap, 
                      () -> workExperienceService.getExperiences(candidateId));
         // Documents
          //  addSection(sections, SectionConstants.DOCUMENTS.getValue(), "Documents", requirementsMap, statusMap,null);
           
          
            // Addresses
            addSection(sections, SectionConstants.ADDRESS.getValue(), "Address History", requirementsMap, statusMap, 
                      () -> null);
             
            // Documents
           // addSection(sections, "documents", "Documents", requirementsMap, statusMap,() -> documentsService.getDocumentsByCandidate(candidateId));
                      
            
            sections = sections.entrySet()
            	    .stream()
            	    .sorted(Map.Entry.comparingByValue(
            	        Comparator.comparingInt(VerificationSectionDTO::getOrder)
            	    ))
            	    .collect(
            	        LinkedHashMap::new,
            	        (m, e) -> m.put(e.getKey(), e.getValue()),
            	        LinkedHashMap::putAll
            	    );

            
        } catch (Exception e) {
        	e.printStackTrace();
            log.error("Error getting sections with status: {}", e.getMessage());
        }
        
        return sections;
    }
    
    */
    
    private Map<String, VerificationSectionDTO> getSectionsWithStatus(
            Long candidateId,
            CandidateVerification verification) {

        Map<String, VerificationSectionDTO> sections = new LinkedHashMap<>();

        try {
            Map<String, Map<String, Object>> requirementsMap = getRequirementsMap(verification);
            Map<String, Map<String, Object>> statusMap =
                    verificationHelperService.getSectionStatusMap(verification);

            for (String sectionKey : requirementsMap.keySet()) {
            	
            	SectionConstants section = SectionConstants.fromNameOrValue(sectionKey);
            	
            	// 🔥 NEW: filter based on verification status
                if (verification.getStatus() == VerificationStatus.ACTION_REQUIRED) {

                    Map<String, Object> sectionStatusMap = statusMap.get(sectionKey);

                    String sectionStatus = sectionStatusMap != null
                            ? (String) sectionStatusMap.get("status")
                            : null;

                    // 👉 Only include sections which are ACTION_REQUIRED
                    if (!"ACTION_REQUIRED".equalsIgnoreCase(sectionStatus)) {
                        continue;
                    }
                }

                switch (section) {

                    case IDENTITY -> addSection(
                            sections,
                            SectionConstants.IDENTITY.getValue(),
                            "Identity",
                            requirementsMap,
                            statusMap,
                            () -> identityService.getIdentityInfo(candidateId)
                    );

                    case EDUCATION -> addSection(
                            sections,
                            SectionConstants.EDUCATION.getValue(),
                            "Education",
                            requirementsMap,
                            statusMap,
                            () -> educationService.getEducations(candidateId)
                    );

                    case WORK_EXPERIENCE -> addSection(
                            sections,
                            SectionConstants.WORK_EXPERIENCE.getValue(),
                            "Work Experience",
                            requirementsMap,
                            statusMap,
                            () -> workExperienceService.getExperiences(candidateId)
                    );

                    case ADDRESS -> addSection(
                            sections,
                            SectionConstants.ADDRESS.getValue(),
                            "Address History",
                            requirementsMap,
                            statusMap,
                            () -> null
                    );

                    default -> log.warn("Unknown section in requirementsMap: {}", sectionKey);
                }
            }

            // ✅ Sorting remains same
            sections = sections.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(
                            Comparator.comparingInt(VerificationSectionDTO::getOrder)
                    ))
                    .collect(
                            LinkedHashMap::new,
                            (m, e) -> m.put(e.getKey(), e.getValue()),
                            LinkedHashMap::putAll
                    );

        } catch (Exception e) {
            log.error("Error getting sections with status: {}", e.getMessage(), e);
        }

        return sections;
    }
    
    private void addSection(Map<String, VerificationSectionDTO> sections, String sectionId, String label,
                           Map<String, Map<String, Object>> requirementsMap,
                           Map<String, Map<String, Object>> statusMap,
                           DataProvider dataProvider) {
        VerificationSectionDTO sectionDTO = new VerificationSectionDTO();
        sectionDTO.setSectionId(sectionId);
        sectionDTO.setLabel(label);
        
        Map<String, Object> requirements = requirementsMap.get(sectionId);
        
        log.info("requirements::::::::::section:::sectionId::{}{}",requirements,sectionId);
        if (requirements != null) {
            sectionDTO.setRequired((boolean) requirements.getOrDefault("required", false));
            sectionDTO.setOrder((Integer) requirements.getOrDefault("order", 0));
            
        }
        log.info("sectionId::::::::::::::::::::::{}",sectionId);
        log.info("statusMap::::::::::::::::::::::{}",statusMap);
        Map<String, Object> statusData = statusMap.get(sectionId);
        if (statusData != null) {
           // sectionDTO.setStatus(SectionStatus.valueOf((String) statusData.getOrDefault("status", "NOT_STARTED")));
        	String statusString = (String) statusData.getOrDefault("status", "NOT_STARTED");
        	sectionDTO.setStatus(SectionStatus.fromString(statusString));
            Object lastUpdated = statusData.get("lastUpdated");
            if (lastUpdated != null) {
                sectionDTO.setLastUpdated(LocalDateTime.parse(lastUpdated.toString()));
            }
        } else {
            sectionDTO.setStatus(SectionStatus.NOT_STARTED);
        }
        
        try {
        	
            Object data = dataProvider!=null?dataProvider.getData():null;
            sectionDTO.setData(data);
        } catch (Exception e) {
        	e.printStackTrace();
            log.warn("Error fetching data for section {}: {}", sectionId, e.getMessage());
        }
      //  sectionDTO.setStatus(SectionStatus.IN_PROGRESS);
        sections.put(sectionId, sectionDTO);
    }
    
    private interface DataProvider {
        Object getData();
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Map<String, Object>> getRequirementsMap(CandidateVerification verification) {
        try {
            if (verification.getSectionRequirements() != null) {
                Map<String, Object> rootMap = objectMapper.readValue(
                    verification.getSectionRequirements(), 
                    new TypeReference<Map<String, Object>>() {}
                );
                
                // Check if we have a nested "sections" structure
                if (rootMap.containsKey("sections")) {
                    return objectMapper.convertValue(
                        rootMap.get("sections"), 
                        new TypeReference<Map<String, Map<String, Object>>>() {}
                    );
                } else {
                    // Already flat structure
                    return objectMapper.convertValue(
                        rootMap, 
                        new TypeReference<Map<String, Map<String, Object>>>() {}
                    );
                }
            }
        } catch (Exception e) {
            log.error("Error parsing section requirements: {}", e.getMessage());
            e.printStackTrace();
        }
        return new LinkedHashMap<>();
    }
    
    
    
    
    
    private void setSectionRequirements(CandidateVerification verification, Long packageId) {
        // This would typically fetch from a package configuration service
        Map<String, Map<String, Object>> requirements = new HashMap<>();
        
        // Basic Details - Always required
       // requirements.put("basicDetails", Map.of("required", true));
        
        // Other sections based on package
        switch (packageId.toString()) {
            case "1": // Basic Package
                requirements.put("identity", Map.of("required", true));
                requirements.put("education", Map.of("required", true));
                requirements.put("documents", Map.of("required", true));
                break;
            case "2": // Standard Package
                requirements.put("identity", Map.of("required", true));
                requirements.put("education", Map.of("required", true));
                requirements.put("workExperience", Map.of("required", true));
                requirements.put("addresses", Map.of("required", true));
                requirements.put("documents", Map.of("required", true));
                break;
            case "3": // Premium Package
                requirements.put("identity", Map.of("required", true));
                requirements.put("education", Map.of("required", true));
                requirements.put("workExperience", Map.of("required", true));
                requirements.put("addresses", Map.of("required", true));
                requirements.put("references", Map.of("required", true));
                requirements.put("court", Map.of("required", true));
                requirements.put("documents", Map.of("required", true));
                break;
            default:
                // Custom package - would fetch from DB
                break;
        }
        
        try {
            verification.setSectionRequirements(objectMapper.writeValueAsString(requirements));
        } catch (Exception e) {
            log.error("Error setting section requirements: {}", e.getMessage());
        }
    }
    
    @Async
    protected void sendVerificationSubmittedNotification(CandidateVerification verification) {
        // Implement notification logic (email, push, etc.)
        log.info("Sending verification submitted notification for candidate: {}", verification.getCandidateId());
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
    
   
    
    
}