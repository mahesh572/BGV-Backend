package com.org.bgv.common;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FilterMetadata {
    private String field;
    private String displayName;
    private String type;
    private Object defaultValue;
    private Object selectedValue;
    private List<Option> options;
}
