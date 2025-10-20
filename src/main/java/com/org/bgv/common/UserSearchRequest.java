package com.org.bgv.common;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchRequest {
    private PaginationRequest pagination;
    private SortingRequest sorting;
    private String search;
    private List<FilterRequest> filters;
}