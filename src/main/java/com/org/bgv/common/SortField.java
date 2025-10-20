package com.org.bgv.common;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SortField {
    private String field;
    private String displayName;
    private String direction;
}