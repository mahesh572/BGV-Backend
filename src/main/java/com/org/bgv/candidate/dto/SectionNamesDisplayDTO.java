package com.org.bgv.candidate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SectionNamesDisplayDTO {

	private String sectionName;
	private Long categoryId;
	private Long checkId;
	
}
