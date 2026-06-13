package com.org.bgv.service;

import java.util.Map;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.entity.ActivityTimeline;
import com.org.bgv.enums.ActivityType;

public class ActivityFactory {

    public static ActivityTimeline create(
            Long caseId,
            Long checkId,
            Long executionId,
            Long documentId,
            Long noteId,
            ActivityType activityType,
            String title,
            String description,
            Long actorId,
            String actorRole,
            String statusFrom,
            String statusTo,
            Map<String,Object> metadata,
            Candidate candidate,
            Long ObjectId) {

        return ActivityTimeline.builder()
                .caseId(caseId)
                .checkId(checkId)
                .objectId(ObjectId)
                .executionId(executionId)
                .documentId(documentId)
                .noteId(noteId)
                .activityType(activityType)
                .title(title)
                .description(description)
                .actorId(actorId)
                .actorRole(actorRole)
                .statusFrom(statusFrom)
                .statusTo(statusTo)
                .metadata(metadata)
                .candidate(candidate)
                .build();
    }
}