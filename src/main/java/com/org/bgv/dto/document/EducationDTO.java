package com.org.bgv.dto.document;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationDTO {
 private Long eduId;
 private String degreeType;
 private String degreeLabel;
 private String fieldOfStudy;
 private String institionName;
 private List<DocumentTypeDto> documentTypes;
}
