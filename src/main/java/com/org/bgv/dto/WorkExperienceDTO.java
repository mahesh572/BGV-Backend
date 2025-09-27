package com.org.bgv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkExperienceDTO {
	private Long experienceId;
    private String companyName;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long employeeId;
    private String managerEmailId;
    private String hrEmailId;
    private String address;
    private String reasonForLeaving;
    private List<DocumentResponse> documents;
  //  private DocumentStats documentStats;
}