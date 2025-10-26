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
	private Long companyId;
	private boolean excludeCompanyUsers = false; 
    private PaginationRequest pagination;
    private SortingRequest sorting;
    private String search;
    private List<FilterRequest> filters;
}