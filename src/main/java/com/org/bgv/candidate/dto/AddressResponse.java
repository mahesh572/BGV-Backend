package com.org.bgv.candidate.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.org.bgv.dto.AddressDTO;
import com.org.bgv.entity.AddressType;
import com.org.bgv.vendor.action.dto.ActionDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {

	private Long checkId;
	private Long caseId;
	private Long categoryId;
	private String status;
	private List<ActionDTO> actions;
	private List<AddressDTO> addresses;
	
	
}
