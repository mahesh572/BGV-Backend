package com.org.bgv.vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldAgentDto {

    private Long userId;

    private String employeeCode;

    private String name;

    private String email;

    private String phone;
}
