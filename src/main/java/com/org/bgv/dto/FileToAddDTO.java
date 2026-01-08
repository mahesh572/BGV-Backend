package com.org.bgv.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileToAddDTO {
    private MultipartFile  file; 
    // In real use: send file identifier OR use MultipartFile in controller
}
