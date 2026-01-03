package com.org.bgv.vendor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObjectDTO {
    
    @JsonProperty("objectId")
    private Long objectId;
    
    @JsonProperty("objectType")
    private String objectType;
    
    @JsonProperty("displayName")
    private String displayName;
    
    @JsonProperty("data")
    private Map<String, Object> data;
    
    @JsonProperty("documentTypes")
    private List<DocumentTypeVerificationDTO> documentTypes;
    
    @JsonProperty("evidence")
    private List<EvidenceDTO> evidence;
}
