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
public class CategoryDocumentsDto {
    private Long categoryId;
    private String categoryName;
    private String categoryCode;
    private String categoryDescription;
    private String categoryLabel;
    private List<DocumentSelectionDto> documents;
}
