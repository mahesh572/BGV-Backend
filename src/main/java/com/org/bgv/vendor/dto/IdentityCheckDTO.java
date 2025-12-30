package com.org.bgv.vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityCheckDTO extends BaseCheckDTO {
    private DeclaredIdentityInfoDTO declaredInfo;
    private IdentityContextDTO context;
}