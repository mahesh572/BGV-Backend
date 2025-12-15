package com.org.bgv.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.org.bgv.common.ActivityStatus;
import com.org.bgv.entity.ActivityTimeline;
import com.org.bgv.entity.Candidate;
import com.org.bgv.repository.ActivityTimelineRepository;
import com.org.bgv.repository.CandidateRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityTimelineService {
    
    private final ActivityTimelineRepository activityRepository;
    private final CandidateRepository candidateRepository;
    
    @Transactional
    public ActivityTimeline addActivity(String candidateUuid, String title, 
                                       String description, ActivityStatus status) {
        
        Candidate candidate = candidateRepository.findByUuid(candidateUuid)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
        
        ActivityTimeline activity = new ActivityTimeline();
        activity.setTitle(title);
        activity.setDescription(description);
        activity.setStatus(status);
        activity.setTimestamp(LocalDateTime.now());
        activity.setCandidate(candidate);
        
        candidate.addActivity(activity);
        
        return activityRepository.save(activity);
    }
    
    public List<ActivityTimeline> getCandidateActivities(String candidateUuid) {
        return activityRepository.findByCandidateUuidOrderByTimestampDesc(candidateUuid);
    }
    
    @Transactional
    public void updateActivityStatus(Long activityId, ActivityStatus newStatus) {
        ActivityTimeline activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        
        activity.setStatus(newStatus);
        activity.setTimestamp(LocalDateTime.now());
        activityRepository.save(activity);
    }
    
    public List<ActivityTimeline> getRecentActivities(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return activityRepository.findAll().stream()
                .filter(activity -> activity.getTimestamp().isAfter(cutoffDate))
                .sorted((a1, a2) -> a2.getTimestamp().compareTo(a1.getTimestamp()))
                .collect(Collectors.toList());
    }
}