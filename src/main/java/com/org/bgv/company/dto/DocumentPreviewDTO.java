package com.org.bgv.company.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentPreviewDTO {

    private Long documentTypeId;
    private String documentName;

    private Boolean selected;
    private Double price;
}
