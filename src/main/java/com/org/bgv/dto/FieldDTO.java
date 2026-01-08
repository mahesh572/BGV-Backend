package com.org.bgv.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldDTO {

	private String name;
    private String label;
    private String type;
    private boolean required;
    private String value;  // can keep as String for text/date
	
}
