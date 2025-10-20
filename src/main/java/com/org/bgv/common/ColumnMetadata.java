package com.org.bgv.common;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ColumnMetadata {
    private String field;
    private String displayName;
    private boolean visible;
}
