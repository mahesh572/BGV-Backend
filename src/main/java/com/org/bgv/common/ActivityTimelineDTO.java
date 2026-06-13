package com.org.bgv.common;

import com.org.bgv.enums.ActivitySeverity;
import com.org.bgv.enums.ActivityType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityTimelineDTO {
    private Long id;
    private String title;
    private String description;
    private String timestamp;
    private String icon;
    private ActivityStatus status;
    private ActivityType type;
    private ActivitySeverity severity; 
    private String actorRole ;
}