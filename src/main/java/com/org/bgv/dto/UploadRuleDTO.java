package com.org.bgv.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadRuleDTO {
    private boolean multiple;
    private boolean required;
}
