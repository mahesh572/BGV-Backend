package com.org.bgv.common;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class PageRequestDto {
    private int page;
    private int size;
    private String sortBy;
    private String sortDirection;
    
    // Default values
    public PageRequestDto() {
        this.page = 0;
        this.size = 10;
        this.sortBy = "userId";
        this.sortDirection = "asc";
    }
}