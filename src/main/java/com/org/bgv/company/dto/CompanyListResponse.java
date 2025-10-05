package com.org.bgv.company.dto;

import com.org.bgv.entity.Company;
import lombok.Data;
import java.util.List;

@Data
public class CompanyListResponse {
    private List<Company> companies;
    private int currentPage;
    private long totalItems;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    
    public CompanyListResponse(List<Company> companies, int currentPage, long totalItems, int totalPages) {
        this.companies = companies;
        this.currentPage = currentPage;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.hasNext = currentPage < totalPages - 1;
        this.hasPrevious = currentPage > 0;
    }
}