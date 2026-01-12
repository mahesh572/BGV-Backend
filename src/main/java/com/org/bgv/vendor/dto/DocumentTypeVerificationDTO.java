package com.org.bgv.vendor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.org.bgv.common.FileDTO;
import com.org.bgv.vendor.action.dto.ActionDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentTypeVerificationDTO {
    
    @JsonProperty("documentTypeId")
    private String documentTypeId;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("status")
    private String status;
    
    private List<ActionDTO> actions;
    
    @JsonProperty("files")
    private List<VerificationFileDTO> files;
}