package com.org.bgv.dto;

import java.util.List;

import com.org.bgv.dto.document.DocumentTypeDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationResponse {
	
	private Long checkId;
	private Long caseId;
	private Long categoryId;
	private String status;
	private List<EducationHistoryDTO> educationhistory;

}
