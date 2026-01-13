package com.org.bgv.vendor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ObjectFieldDTO {
    private String code;       // HOUSE_NO, COMPANY_NAME
    private String label;      // "House No", "Company Name"
    private String type;       // TEXT, DATE, NUMBER, BOOLEAN
    private Object value;
    private boolean mandatory;
    private boolean readOnly;
}
