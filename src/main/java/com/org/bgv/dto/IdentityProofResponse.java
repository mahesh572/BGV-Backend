package com.org.bgv.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class IdentityProofResponse {
    private Long profileId;
    private List<IdentityProofDTO> proofs;
    private DocumentStats summary;
}
