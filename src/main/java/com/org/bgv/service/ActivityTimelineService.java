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

	private final ActivityTimelineRepository activityTimelineRepository;

    public void log(ActivityTimeline activity) {
    	activityTimelineRepository.save(activity);
    }

    
    
    public List<ActivityTimelineDTO> getTimeline(
            Long checkId,
            Long objectId) {

        return activityTimelineRepository
                .findByCheckIdAndObjectIdOrderByCreatedAtDesc(
                        checkId,
                        objectId
                )
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ActivityTimelineDTO toDTO(ActivityTimeline activity) {

        return ActivityTimelineDTO.builder()
                .id(activity.getId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .timestamp(
                        activity.getCreatedAt()
                                .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))
                )
               // .icon(getIcon(activity))
                .status(
                        activity.getActivityStatus() != null
                                ? activity.getActivityStatus()
                                : null
                )
                .type(activity.getActivityType())
                .severity(
                        activity.getSeverity() != null
                                ? activity.getSeverity()
                                : null
                )
                .actorRole(activity.getActorRole())
                .build();
    }
   
}