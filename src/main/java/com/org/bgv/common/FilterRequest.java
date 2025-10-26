package com.org.bgv.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequest {
    private String field;
    private Object selectedValue;
    private Boolean isSelected;
}