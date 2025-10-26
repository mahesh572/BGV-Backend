package com.org.bgv.common;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PaginationMetadata {
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<Integer> allowedPageSizes;
}