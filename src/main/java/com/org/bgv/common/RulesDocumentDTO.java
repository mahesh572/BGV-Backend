package com.org.bgv.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RulesDocumentDTO {
	 private Long documentTypeId;
	    private String name;
	    private String code;
	    private Boolean includedInBase;
	    private Boolean required;
	    private Double addonPrice;
	    private Boolean selected;
}
