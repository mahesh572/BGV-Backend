package com.org.bgv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTypeResponse {
    private Long categoryId;
    private Long doc_type_id;
    private String name;
    private String categoryName; // Optional: include category name in response
}
