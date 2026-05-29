package com.org.bgv.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.repository.CandidateRepository;
import com.org.bgv.common.ActivityStatus;
import com.org.bgv.common.ActivityTimelineDTO;
import com.org.bgv.entity.ActivityTimeline;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.repository.ActivityTimelineRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityTimelineService {

    private final ActivityTimelineRepository repository;

    // 🔹 Generic creator
    public void createActivity(ActivityTimeline activity) {
        repository.save(activity);
    }

    // 🔹 Case Events
    public void logCaseCreated(VerificationCase verificationCase) {

        createActivity(ActivityTimeline.builder()
                .caseId(verificationCase.getCaseId())
              //  .candidateId(verificationCase.getCandidateId())
                .title("Case Initiated")
                .description("Verification case " + verificationCase.getCaseNumber() + " created")
                .type("CASE_EVENT")
                .status("COMPLETED")
                .action("CASE_CREATED")
                .build());
    }

    public void logCaseCompleted(VerificationCase verificationCase) {

        createActivity(ActivityTimeline.builder()
                .caseId(verificationCase.getCaseId())
             //   .candidateId(verificationCase.getCandidateId())
                .title("Case Completed")
                .description("All verification checks completed")
                .type("CASE_EVENT")
                .status("COMPLETED")
                .action("CASE_COMPLETED")
                .build());
    }

    // 🔹 Check Events
    public void logCheckStatusChange(VerificationCaseCheck check) {

        String status = check.getStatus() != null
                ? check.getStatus().name()
                : "PENDING";

        String category = check.getCategory() != null
                ? check.getCategory().getName()
                : "Check";

        createActivity(ActivityTimeline.builder()
                .caseId(check.getVerificationCase().getCaseId())
               // .candidateId(check.getVerificationCase().getCandidateId())
                .checkId(check.getCaseCheckId())
                .title(category + " Check")
                .description(category + " moved to " + status)
                .type("CHECK_EVENT")
                .status(status)
                .action("CHECK_STATUS_UPDATED")
                .build());
    }

    // 🔹 Document Upload
    public void logDocumentUpload(Long caseId, Long candidateId, String docName) {

        createActivity(ActivityTimeline.builder()
                .caseId(caseId)
               // .candidateId(candidateId)
                .title("Document Uploaded")
                .description(docName + " uploaded")
                .type("USER_ACTION")
                .status("COMPLETED")
                .action("DOCUMENT_UPLOAD")
                .build());
    }

    // 🔹 Fetch timeline for case
    public List<ActivityTimelineDTO> getCaseTimeline(Long caseId) {

        List<ActivityTimeline> activities =
                repository.findByCaseIdOrderByTimestampAsc(caseId);

        return activities.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // 🔹 Mapper
    private ActivityTimelineDTO mapToDTO(ActivityTimeline activity) {

        return ActivityTimelineDTO.builder()
                .id(activity.getId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .timestamp(format(activity.getTimestamp()))
                .icon(resolveIcon(activity.getAction()))
                .status(activity.getStatus())
                .type(activity.getType())
                .build();
    }

    private String format(LocalDateTime time) {
        return time != null
                ? time.format(DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a"))
                : null;
    }

    private String resolveIcon(String action) {
        if (action == null) return "📝";

        return switch (action) {
            case "CASE_CREATED" -> "📁";
            case "CASE_COMPLETED" -> "✅";
            case "DOCUMENT_UPLOAD" -> "📄";
            case "CHECK_STATUS_UPDATED" -> "🔍";
            default -> "📝";
        };
    }
}