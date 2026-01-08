package com.org.bgv.common;

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
    private String status;
    private String type;
}