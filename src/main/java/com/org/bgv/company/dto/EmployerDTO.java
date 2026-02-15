package com.org.bgv.company.dto;

import java.time.LocalDate;
import java.util.List;

import com.org.bgv.common.UserDto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerDTO {
	
	    private Long id;
	    private String companyname;
	    private CompanyType companyType;
	    private IndustryType industry;
	    private CompanySize companySize;
	    private String status;
}
