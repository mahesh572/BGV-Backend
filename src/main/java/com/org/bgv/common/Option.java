package com.org.bgv.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Option {
    private String label;
    private String value;
    private Boolean isSelected;

   
}
