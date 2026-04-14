package com.org.bgv.candidate.dto;

import java.util.List;

import com.org.bgv.dto.EducationHistoryDTO;
import com.org.bgv.dto.EducationResponse;

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
public class IdentityResponse {
	private Long checkId;
	private Long caseId;
	private Long categoryId;
	private String status;
	private List<IdentityObjectResponse> identityhistory;
}
