package com.org.bgv.common;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SortingMetadata {
    private SortField currentSort;
    private List<SortField> sortableFields;
}