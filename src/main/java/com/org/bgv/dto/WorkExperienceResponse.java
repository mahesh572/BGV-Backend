package com.org.bgv.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkExperienceResponse {
    private Long profileId;
    private String profileName;
    private List<WorkExperienceDTO> workExperiences;
    private DocumentSummary summary;
}