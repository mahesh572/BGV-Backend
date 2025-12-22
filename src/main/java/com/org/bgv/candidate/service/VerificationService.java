package com.org.bgv.candidate.service;



import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.bgv.candidate.dto.CandidateVerificationDTO;
import com.org.bgv.candidate.dto.VerificationSectionDTO;
import com.org.bgv.candidate.entity.CandidateVerification;
import com.org.bgv.candidate.repository.CandidateVerificationRepository;
import com.org.bgv.constants.SectionStatus;
import com.org.bgv.constants.VerificationStatus;
import com.org.bgv.service.DocumentService;
import com.org.bgv.service.EducationService;
import com.org.bgv.service.IdentityProofService;
import com.org.bgv.service.ProfileAddressService;
import com.org.bgv.service.ProfileService;
import com.org.bgv.service.WorkExperienceService;

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
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationService {
    
    private final CandidateVerificationRepository verificationRepository;
    private final ObjectMapper objectMapper;
    private final ProfileService profileService;
    private final EducationService educationService;
    private final WorkExperienceService workExperienceService;
    private final IdentityProofService identityService;
    private final ProfileAddressService addressService;
    private final DocumentService documentsService;
    
    @Cacheable(value = "verification", key = "#candidateId")
    public CandidateVerificationDTO getCandidateVerification(Long candidateId) {
        log.info("Fetching verification for candidate: {}", candidateId);
        
        CandidateVerification verification = verificationRepository.findByCandidateId(candidateId)
            .orElseThrow(() -> new EntityNotFoundException("Verification not found for candidate: " + candidateId));
        
        CandidateVerificationDTO dto = convertToDTO(verification);
        
        // Calculate progress
        dto.setProgressPercentage(calculateProgress(verification));
        
        // Get section requirements and status
        Map<String, VerificationSectionDTO> sections = getSectionsWithStatus(candidateId, verification);
        dto.setSections(sections);
        
        return dto;
    }
    
    @Transactional
    @CacheEvict(value = "verification", key = "#candidateId")
    public CandidateVerificationDTO updateSectionStatus(Long candidateId, String section, 
                                                        SectionStatus status, Object data) throws ValidationException {
        log.info("Updating section {} status to {} for candidate: {}", section, status, candidateId);
        
        CandidateVerification verification = verificationRepository.findByCandidateId(candidateId)
            .orElseThrow(() -> new EntityNotFoundException("Verification not found"));
        
        // Update section status in JSON field
        Map<String, Map<String, Object>> sectionStatusMap = getSectionStatusMap(verification);
        Map<String, Object> sectionData = sectionStatusMap.getOrDefault(section, new HashMap<>());
        
        sectionData.put("status", status.toString());
        sectionData.put("lastUpdated", LocalDateTime.now().toString());
        if (data != null) {
            sectionData.put("data", data);
        }
        
        sectionStatusMap.put(section, sectionData);
        
        try {
            verification.setSectionStatus(objectMapper.writeValueAsString(sectionStatusMap));
            verification.setUpdatedAt(LocalDateTime.now());
            verification = verificationRepository.save(verification);
        } catch (Exception e) {
            log.error("Error updating section status: {}", e.getMessage());
            throw new ValidationException("Failed to update section status");
        }
        
        // Update overall progress
        int progress = calculateProgress(verification);
        verification.setProgressPercentage(progress);
        
        // Update verification status if all required sections are completed
        if (progress == 100 && verification.getStatus() == VerificationStatus.IN_PROGRESS) {
            verification.setStatus(VerificationStatus.SUBMITTED);
            verification.setSubmittedAt(LocalDateTime.now());
        }
        
        verification = verificationRepository.save(verification);
        
        return convertToDTO(verification);
    }
    
    @Transactional
    @CacheEvict(value = "verification", key = "#candidateId")
    public CandidateVerificationDTO submitForVerification(Long candidateId) throws ValidationException {
        log.info("Submitting verification for candidate: {}", candidateId);
        
        CandidateVerification verification = verificationRepository.findByCandidateId(candidateId)
            .orElseThrow(() -> new EntityNotFoundException("Verification not found"));
        
        // Check if all required sections are completed
        int progress = calculateProgress(verification);
        if (progress < 100) {
            throw new ValidationException("Cannot submit verification. Please complete all required sections. Progress: " + progress + "%");
        }
        
        verification.setStatus(VerificationStatus.SUBMITTED);
        verification.setSubmittedAt(LocalDateTime.now());
        verification.setUpdatedAt(LocalDateTime.now());
        
        verification = verificationRepository.save(verification);
        
        // Trigger notification
        sendVerificationSubmittedNotification(verification);
        
        return convertToDTO(verification);
    }
    
    @Transactional
    @CacheEvict(value = "verification", key = "#candidateId")
    public CandidateVerificationDTO createVerification(Long candidateId, CandidateVerificationDTO request) throws ValidationException {
        log.info("Creating verification for candidate: {}", candidateId);
        
        // Check if verification already exists
        if (verificationRepository.findByCandidateId(candidateId).isPresent()) {
            throw new ValidationException("Verification already exists for this candidate");
        }
        
        CandidateVerification verification = new CandidateVerification();
        verification.setCandidateId(candidateId);
        verification.setPackageId(request.getPackageId());
        verification.setPackageName(request.getPackageName());
        verification.setEmployerName(request.getEmployerName());
        verification.setEmployerId(request.getEmployerId());
        verification.setDueDate(request.getDueDate());
        verification.setStartDate(LocalDateTime.now());
        verification.setStatus(VerificationStatus.IN_PROGRESS);
        verification.setProgressPercentage(0);
        verification.setInstructions(request.getInstructions());
        verification.setSupportEmail(request.getSupportEmail());
        
        // Set section requirements based on package
        setSectionRequirements(verification, request.getPackageId());
        
        verification = verificationRepository.save(verification);
        
        return convertToDTO(verification);
    }
    
    private int calculateProgress(CandidateVerification verification) {
        try {
            Map<String, Map<String, Object>> sectionStatusMap = getSectionStatusMap(verification);
            Map<String, Map<String, Object>> requirementsMap = getRequirementsMap(verification);
            
            int totalRequired = 0;
            int completed = 0;
            
            for (Map.Entry<String, Map<String, Object>> entry : requirementsMap.entrySet()) {
                boolean required = (boolean) entry.getValue().getOrDefault("required", false);
                if (required) {
                    totalRequired++;
                    
                    Map<String, Object> statusData = sectionStatusMap.get(entry.getKey());
                    if (statusData != null) {
                        String status = (String) statusData.getOrDefault("status", SectionStatus.NOT_STARTED.toString());
                        if (SectionStatus.COMPLETED.toString().equals(status) || 
                            SectionStatus.VERIFIED.toString().equals(status)) {
                            completed++;
                        }
                    }
                }
            }
            
            return totalRequired > 0 ? (completed * 100) / totalRequired : 0;
        } catch (Exception e) {
            log.error("Error calculating progress: {}", e.getMessage());
            return 0;
        }
    }
    
    private Map<String, VerificationSectionDTO> getSectionsWithStatus(Long candidateId, CandidateVerification verification) {
        Map<String, VerificationSectionDTO> sections = new HashMap<>();
        
        try {
            Map<String, Map<String, Object>> requirementsMap = getRequirementsMap(verification);
            Map<String, Map<String, Object>> statusMap = getSectionStatusMap(verification);
            
            // Basic Details
            addSection(sections, "basicDetails", "Basic Details", requirementsMap, statusMap, 
                      () -> profileService.getBasicDetails(candidateId));
            
            // Education
            addSection(sections, "education", "Education", requirementsMap, statusMap, 
                      () -> educationService.getEducations(candidateId));
            
            // Work Experience
            addSection(sections, "workExperience", "Work Experience", requirementsMap, statusMap, 
                      () -> workExperienceService.getExperiences(candidateId));
            
            // Identity
            addSection(sections, "identity", "Identity", requirementsMap, statusMap, 
                      () -> identityService.getIdentityInfo(candidateId));
           /* 
            // Addresses
            addSection(sections, "addresses", "Address History", requirementsMap, statusMap, 
                      () -> addressService.getAddresses(candidateId));
            
            // Documents
            addSection(sections, "documents", "Documents", requirementsMap, statusMap, 
                      () -> documentsService.getDocumentsByCandidate(candidateId));
                      */
            
        } catch (Exception e) {
            log.error("Error getting sections with status: {}", e.getMessage());
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
        if (requirements != null) {
            sectionDTO.setRequired((boolean) requirements.getOrDefault("required", false));
        }
        
        Map<String, Object> statusData = statusMap.get(sectionId);
        if (statusData != null) {
            sectionDTO.setStatus(SectionStatus.valueOf((String) statusData.getOrDefault("status", "NOT_STARTED")));
            Object lastUpdated = statusData.get("lastUpdated");
            if (lastUpdated != null) {
                sectionDTO.setLastUpdated(LocalDateTime.parse(lastUpdated.toString()));
            }
        } else {
            sectionDTO.setStatus(SectionStatus.NOT_STARTED);
        }
        
        try {
            Object data = dataProvider.getData();
            sectionDTO.setData(data);
        } catch (Exception e) {
            log.warn("Error fetching data for section {}: {}", sectionId, e.getMessage());
        }
        
        sections.put(sectionId, sectionDTO);
    }
    
    private interface DataProvider {
        Object getData();
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Map<String, Object>> getRequirementsMap(CandidateVerification verification) {
        try {
            if (verification.getSectionRequirements() != null) {
                return objectMapper.readValue(verification.getSectionRequirements(), 
                    new TypeReference<Map<String, Map<String, Object>>>() {});
            }
        } catch (Exception e) {
            log.error("Error parsing section requirements: {}", e.getMessage());
        }
        return new HashMap<>();
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Map<String, Object>> getSectionStatusMap(CandidateVerification verification) {
        try {
            if (verification.getSectionStatus() != null) {
                return objectMapper.readValue(verification.getSectionStatus(), 
                    new TypeReference<Map<String, Map<String, Object>>>() {});
            }
        } catch (Exception e) {
            log.error("Error parsing section status: {}", e.getMessage());
        }
        return new HashMap<>();
    }
    
    private void setSectionRequirements(CandidateVerification verification, Long packageId) {
        // This would typically fetch from a package configuration service
        Map<String, Map<String, Object>> requirements = new HashMap<>();
        
        // Basic Details - Always required
        requirements.put("basicDetails", Map.of("required", true));
        
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
        dto.setPackageId(verification.getPackageId());
        dto.setPackageName(verification.getPackageName());
        dto.setEmployerName(verification.getEmployerName());
        dto.setEmployerId(verification.getEmployerId());
        dto.setDueDate(verification.getDueDate());
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