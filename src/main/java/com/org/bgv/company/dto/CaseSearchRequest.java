package com.org.bgv.company.dto;



import java.util.List;

import com.org.bgv.common.FilterRequest;
import com.org.bgv.common.PaginationRequest;
import com.org.bgv.common.SortingRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaseSearchRequest {

    // 🔹 Mandatory (from controller)
    private Long companyId;

    // 🔹 Optional filters
    private Long candidateId;

    // 🔹 Free text search (case number)
    private String search;

    // 🔹 Generic UI-driven filters
    private List<FilterRequest> filters;

    // 🔹 Pagination
    private PaginationRequest pagination;

    // 🔹 Sorting
    private SortingRequest sorting;
    
    
    

}
