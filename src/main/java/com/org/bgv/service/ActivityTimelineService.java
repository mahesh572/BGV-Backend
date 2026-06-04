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

    public void log(ActivityTimeline activity) {
        repository.save(activity);
    }

   
}