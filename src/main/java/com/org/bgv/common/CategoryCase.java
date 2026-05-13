package com.org.bgv.common;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCase {
	@NotNull
    private Long categoryId;
    
	private List<Long> selectedRuleIds;  // going to be removed
	
	private List<SelectedRuleRequest> selectedRules;
	
	private List<Long> selectedDocumentIds;
   
    private List<CaseDocumentSelection> documents;
}
