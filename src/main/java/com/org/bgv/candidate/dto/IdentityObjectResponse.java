package com.org.bgv.candidate.dto;

import java.util.List;

import com.org.bgv.dto.EducationHistoryDTO;
import com.org.bgv.dto.FieldDTO;
import com.org.bgv.dto.document.DocumentTypeDto;
import com.org.bgv.dto.document.FileDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdentityObjectResponse {
	private Long id;
    private String type;  // AADHAR
    private String label; // display name
    private Long typeId;
    private List<FieldDTO> fields;
   // private List<FileDTO> files;
   // private Integer maxfiles;
   // private String typeLabel;
    private List<DocumentTypeDto> documentTypes;
}
