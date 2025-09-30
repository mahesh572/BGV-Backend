package com.org.bgv.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequestDto {
    private Long categoryId;
    private Long documentTypeId;
    private String companyId; // Optional, only for work experience
    private String customTypeName; // Optional, for "Other" category
    private MultipartFile file;
}