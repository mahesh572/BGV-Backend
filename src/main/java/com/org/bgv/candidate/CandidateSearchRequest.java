package com.org.bgv.candidate;

import java.util.List;

import com.org.bgv.common.FilterRequest;
import com.org.bgv.common.PaginationRequest;
import com.org.bgv.common.SortingRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CandidateSearchRequest {
    private Long companyId;
    private PaginationRequest pagination;
    private SortingRequest sorting;
    private String search;
    private List<FilterRequest> filters;
}
