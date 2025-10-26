package com.org.bgv.recruitement.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostDTO {

    private String title;
    private String department;
    private String description;
    private String requirements;
    private String responsibilities;
    private List<String> skills;

    private LocationDTO location;
    private SalaryDTO salary;

    private String employmentType;      // e.g. full-time, part-time
    private String experienceLevel;     // e.g. junior, mid-level, senior
    private String educationLevel;      // e.g. bachelors, masters
    private Integer vacancies;
    private LocalDate applicationDeadline;
    private String status;              // e.g. active, closed, draft

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LocationDTO {
        private String city;
        private String state;
        private String country;
        private boolean remote;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalaryDTO {
        private String min;
        private String max;
        private String currency; // e.g. USD, INR
        private String period;   // e.g. yearly, monthly
    }
}

