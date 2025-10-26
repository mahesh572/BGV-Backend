package com.org.bgv.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CheckTypeResponse {
    private Long id;
    private String name;
    private String label;
}