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
	private Long checkId;
	private Long caseId;
	private Long categoryId;
    private List<WorkExperienceDTO> workExperiences;
   
}