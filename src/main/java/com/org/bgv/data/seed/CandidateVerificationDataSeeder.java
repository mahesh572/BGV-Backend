package com.org.bgv.data.seed;



import com.org.bgv.candidate.entity.CandidateVerification;
import com.org.bgv.candidate.repository.CandidateVerificationRepository;
import com.org.bgv.constants.VerificationStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CandidateVerificationDataSeeder implements CommandLineRunner {

    private final CandidateVerificationRepository candidateVerificationRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedCandidateVerificationData();
    }

    private void seedCandidateVerificationData() {
        log.info("Starting Candidate Verification data seeding...");

        // Check if data already exists for candidateId 5
        boolean exists = candidateVerificationRepository.existsByCandidateId(5L);
        
        if (exists) {
            log.info("Candidate verification data for candidateId 5 already exists. Skipping...");
            return;
        }

        try {
            // Create sample verification records for candidateId 5
          //  List<CandidateVerification> verifications = createSampleVerifications();
            
         //   candidateVerificationRepository.saveAll(verifications);
            
            log.info("Successfully seeded candidate verification data for candidateId 5");
        //    log.info("Created {} verification records", verifications.size());
            
        } catch (Exception e) {
            log.error("Error seeding candidate verification data: {}", e.getMessage(), e);
        }
    }

    private List<CandidateVerification> createSampleVerifications() throws Exception {
        // Current date and time
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = now.plusDays(30);
        LocalDateTime startDate = now.minusDays(5);
        
        // Create 3 different verification packages for candidate 5
        return Arrays.asList(
            // 1. Standard Background Check (Active/In Progress)
            createStandardBackgroundCheck(now, dueDate, startDate),
            
            // 2. Comprehensive Verification (Submitted)
            createComprehensiveVerification(now, dueDate, startDate),
            
            // 3. Quick Verification (Completed)
            createQuickVerification(now, dueDate, startDate)
        );
    }

    private CandidateVerification createStandardBackgroundCheck(LocalDateTime now, LocalDateTime dueDate, LocalDateTime startDate) throws Exception {
        // Create the CORRECT section requirements structure
        ObjectNode requirements = objectMapper.createObjectNode();
        
        // Direct structure without "sections" wrapper
        requirements.putObject("basicDetails")
                    .put("required", true)
                    .put("description", "Personal information verification");
        
        requirements.putObject("education")
                    .put("required", true)
                    .put("description", "Educational background check");
        
        requirements.putObject("workExperience")
                    .put("required", true)
                    .put("description", "Employment history verification");
        
        requirements.putObject("addresses")
                    .put("required", true)
                    .put("description", "Address history");
        
        requirements.putObject("documents")
                    .put("required", true)
                    .put("description", "Document verification");
        
        requirements.putObject("identity")
                    .put("required", true)
                    .put("description", "Identity verification");
        
        // Create section status JSON (separate from requirements)
        ObjectNode statusRoot = objectMapper.createObjectNode();
        
        statusRoot.putObject("basicDetails")
                 .put("status", "completed")
                 .put("progress", 100);
        
        statusRoot.putObject("education")
                 .put("status", "in-progress")
                 .put("progress", 60);
        
        statusRoot.putObject("workExperience")
                 .put("status", "pending")
                 .put("progress", 0);
        
        statusRoot.putObject("addresses")
                 .put("status", "pending")
                 .put("progress", 0);
        
        statusRoot.putObject("documents")
                 .put("status", "pending")
                 .put("progress", 0);
        
        statusRoot.putObject("identity")
                 .put("status", "in-progress")
                 .put("progress", 30);

        return CandidateVerification.builder()
                .candidateId(5L)
               // .packageId(101L)
               // .packageName("Standard Background Check")
               // .employerName("TechCorp Solutions")
              //  .employerId("TCS-2024-001")
             //   .dueDate(dueDate)
                .startDate(startDate)
                .status(VerificationStatus.IN_PROGRESS)
                .progressPercentage(45)
                .instructions("Please complete all required sections by the due date.")
                .supportEmail("verification@techcorp.com")
                .verificationNotes("Candidate is cooperative. Education verification in progress.")
                .sectionRequirements(requirements.toString())  // Direct structure
                .sectionStatus(statusRoot.toString())          // Status structure
                .createdBy("system")
                .updatedBy("system")
                .build();
    }
    private CandidateVerification createComprehensiveVerification(LocalDateTime now, LocalDateTime dueDate, LocalDateTime startDate) throws Exception {
        // Create section requirements JSON
        ObjectNode sectionRequirements = objectMapper.createObjectNode();
        ObjectNode sections = sectionRequirements.putObject("sections");
        
        sections.putObject("basicDetails")
                .put("required", true)
                .put("description", "Complete personal information");
        
        sections.putObject("education")
                .put("required", true)
                .put("description", "All educational qualifications");
        
        sections.putObject("workExperience")
                .put("required", true)
                .put("description", "Complete employment history with references");
        
        sections.putObject("addresses")
                .put("required", true)
                .put("description", "10 years address history");
        
        sections.putObject("documents")
                .put("required", true)
                .put("description", "All supporting documents");
        
        sections.putObject("identity")
                .put("required", true)
                .put("description", "Biometric verification");
        
        sections.putObject("criminalRecord")
                .put("required", true)
                .put("description", "Criminal background check");
        
        sections.putObject("creditHistory")
                .put("required", false)
                .put("description", "Credit history verification (optional)");

        // Create section status JSON - All completed
        ObjectNode sectionStatus = objectMapper.createObjectNode();
        
        LocalDateTime submittedTime = now.minusDays(10);
        
        sectionStatus.putObject("basicDetails")
                     .put("status", "completed")
                     .put("completedAt", submittedTime.minusDays(2).toString())
                     .put("verifiedBy", "agent_001")
                     .put("progress", 100);
        
        sectionStatus.putObject("education")
                     .put("status", "completed")
                     .put("completedAt", submittedTime.minusDays(1).toString())
                     .put("verifiedBy", "agent_002")
                     .put("progress", 100)
                     .put("notes", "All degrees verified");
        
        sectionStatus.putObject("workExperience")
                     .put("status", "completed")
                     .put("completedAt", submittedTime.toString())
                     .put("verifiedBy", "agent_003")
                     .put("progress", 100)
                     .put("notes", "References checked");
        
        sectionStatus.putObject("addresses")
                     .put("status", "completed")
                     .put("completedAt", submittedTime.minusHours(12).toString())
                     .put("verifiedBy", "system")
                     .put("progress", 100);
        
        sectionStatus.putObject("documents")
                     .put("status", "completed")
                     .put("completedAt", submittedTime.minusHours(6).toString())
                     .put("verifiedBy", "agent_001")
                     .put("progress", 100);
        
        sectionStatus.putObject("identity")
                     .put("status", "completed")
                     .put("completedAt", submittedTime.minusDays(1).toString())
                     .put("verifiedBy", "biometric_system")
                     .put("progress", 100);
        
        sectionStatus.putObject("criminalRecord")
                     .put("status", "completed")
                     .put("completedAt", submittedTime.toString())
                     .put("verifiedBy", "vendor_fis")
                     .put("progress", 100)
                     .put("notes", "No criminal record found");
        
        sectionStatus.putObject("creditHistory")
                     .put("status", "not-required")
                     .put("progress", 0)
                     .put("notes", "Optional section skipped");

        return CandidateVerification.builder()
                .candidateId(5L)
              //  .packageId(201L)
              //  .packageName("Comprehensive Verification")
              //  .employerName("Global Finance Inc.")
              //  .employerId("GFI-EMP-789")
              //  .dueDate(dueDate.minusDays(15)) // Past due date
                .startDate(startDate.minusDays(20))
                .status(VerificationStatus.SUBMITTED)
                .progressPercentage(100)
                .instructions("This is a comprehensive background verification. " +
                             "Please provide accurate information. " +
                             "All documents will be verified by third-party agencies.")
                .supportEmail("bgv.support@globalfinance.com")
                .submittedAt(submittedTime)
                .completedAt(null) // Not completed yet (under review)
                .verificationNotes("Candidate has submitted all required information. " +
                                  "Waiting for vendor verification reports. " +
                                  "Expected completion: 3 business days.")
                .sectionRequirements(sectionRequirements.toString())
                .sectionStatus(sectionStatus.toString())
                .createdBy("hr_manager")
                .updatedBy("system")
                .build();
    }

    private CandidateVerification createQuickVerification(LocalDateTime now, LocalDateTime dueDate, LocalDateTime startDate) throws Exception {
        // Create section requirements JSON
        ObjectNode sectionRequirements = objectMapper.createObjectNode();
        ObjectNode sections = sectionRequirements.putObject("sections");
        
        sections.putObject("basicDetails")
                .put("required", true)
                .put("description", "Basic personal information");
        
        sections.putObject("education")
                .put("required", true)
                .put("description", "Highest degree verification");
        
        sections.putObject("workExperience")
                .put("required", true)
                .put("description", "Current employment verification");
        
        sections.putObject("identity")
                .put("required", true)
                .put("description", "ID verification");

        // Create section status JSON - All completed
        ObjectNode sectionStatus = objectMapper.createObjectNode();
        
        LocalDateTime completedTime = now.minusDays(30);
        LocalDateTime submittedTime = completedTime.minusDays(2);
        
        sectionStatus.putObject("basicDetails")
                     .put("status", "completed")
                     .put("completedAt", submittedTime.minusDays(1).toString())
                     .put("verifiedBy", "system")
                     .put("progress", 100)
                     .put("verificationMethod", "auto");
        
        sectionStatus.putObject("education")
                     .put("status", "completed")
                     .put("completedAt", submittedTime.toString())
                     .put("verifiedBy", "vendor_educheck")
                     .put("progress", 100)
                     .put("notes", "Degree verified from university database");
        
        sectionStatus.putObject("workExperience")
                     .put("status", "completed")
                     .put("completedAt", submittedTime.plusHours(6).toString())
                     .put("verifiedBy", "agent_005")
                     .put("progress", 100)
                     .put("notes", "Employment confirmed with HR");
        
        sectionStatus.putObject("identity")
                     .put("status", "completed")
                     .put("completedAt", submittedTime.plusHours(12).toString())
                     .put("verifiedBy", "id_verification_system")
                     .put("progress", 100)
                     .put("verificationMethod", "digital");

        return CandidateVerification.builder()
                .candidateId(5L)
              //  .packageId(301L)
              //  .packageName("Quick Employment Check")
              //  .employerName("StartUp Ventures")
              //  .employerId("SUV-HR-456")
             //   .dueDate(dueDate.minusDays(40)) // Past due date
                .startDate(startDate.minusDays(45))
                .status(VerificationStatus.COMPLETED)
                .progressPercentage(100)
                .instructions("Quick verification for employment purposes only. " +
                             "Complete within 48 hours for fast processing.")
                .supportEmail("verify@startupventures.com")
                .submittedAt(submittedTime)
                .completedAt(completedTime)
                .verificationNotes("All checks passed successfully. " +
                                  "Verification completed within SLA. " +
                                  "Candidate cleared for employment. " +
                                  "Report generated and shared with employer.")
                .sectionRequirements(sectionRequirements.toString())
                .sectionStatus(sectionStatus.toString())
                .createdBy("recruiter_ai")
                .updatedBy("system")
                .build();
    }
}