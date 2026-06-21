package com.org.bgv.vendor.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentVerificationDto {

    private Long id;

    private String checkType;

    private String status;

    private LocalDateTime updatedAt;

}