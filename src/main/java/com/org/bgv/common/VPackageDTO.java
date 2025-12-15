package com.org.bgv.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VPackageDTO {
	private String name;
    private String id;
    private String price;
    
    @JsonProperty("timeline")
    private String timeline;
    
    private String status;
    
    @JsonProperty("assignedDate")
    private String assignedDate;
}
