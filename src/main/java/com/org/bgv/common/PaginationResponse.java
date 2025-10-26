package com.org.bgv.common;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponse<T> {
    private List<T> content;
    private PaginationMetadata pagination;
    private SortingMetadata sorting;
    private List<FilterMetadata> filters;
    private List<ColumnMetadata> columns;
}