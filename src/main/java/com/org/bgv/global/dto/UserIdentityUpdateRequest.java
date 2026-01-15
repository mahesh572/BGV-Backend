package com.org.bgv.global.dto;

import java.util.List;

import com.org.bgv.dto.DocumentUploadRequest;
import com.org.bgv.dto.FieldDTO;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UserIdentityUpdateRequest {
	private Long id;
    private String type;  // AADHAR
    private String label; // display name
    private Long typeId;
    private List<FieldDTO> fields;
}
