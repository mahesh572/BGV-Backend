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
	private Long id;
    private String companyName;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long employeeId;
    private String managerEmail;
    private String hrEmail;
    private String companyAddress;
    private String reasonForLeaving;
    private Boolean currentlyWorking;
    private String city;
    private String country;
    private String state;
    private String noticePeriod;
    private String employmentType;
    private List<DocumentResponse> documents;
  //  private DocumentStats documentStats;
}