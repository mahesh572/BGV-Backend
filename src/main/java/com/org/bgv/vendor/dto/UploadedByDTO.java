package com.org.bgv.vendor.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class UploadedByDTO {

    private Long id;
    private String role; // VENDOR / CANDIDATE / ADMIN
}

