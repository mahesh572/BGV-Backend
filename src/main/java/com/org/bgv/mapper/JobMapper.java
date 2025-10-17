package com.org.bgv.mapper;

import com.org.bgv.entity.Job;
import com.org.bgv.recruitement.dto.JobPostDTO;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class JobMapper {

    // Convert JobPostDTO to Job Entity
    public Job toEntity(JobPostDTO jobPostDTO) {
        if (jobPostDTO == null) {
            return null;
        }

        return Job.builder()
                .title(jobPostDTO.getTitle())
                .department(jobPostDTO.getDepartment())
                .description(jobPostDTO.getDescription())
                .requirements(jobPostDTO.getRequirements())
                .responsibilities(jobPostDTO.getResponsibilities())
                .skills(jobPostDTO.getSkills() != null ? new ArrayList<>(jobPostDTO.getSkills()) : new ArrayList<>())
                .location(toLocationEntity(jobPostDTO.getLocation()))
                .salary(toSalaryEntity(jobPostDTO.getSalary()))
                .employmentType(jobPostDTO.getEmploymentType())
                .experienceLevel(jobPostDTO.getExperienceLevel())
                .educationLevel(jobPostDTO.getEducationLevel())
                .vacancies(jobPostDTO.getVacancies())
                .applicationDeadline(toLocalDateTime(jobPostDTO.getApplicationDeadline()))
                .status(jobPostDTO.getStatus())
                .build();
    }

    // Convert Job Entity to JobPostDTO
    public JobPostDTO toDTO(Job job) {
        if (job == null) {
            return null;
        }

        return JobPostDTO.builder()
                .title(job.getTitle())
                .department(job.getDepartment())
                .description(job.getDescription())
                .requirements(job.getRequirements())
                .responsibilities(job.getResponsibilities())
                .skills(job.getSkills() != null ? new ArrayList<>(job.getSkills()) : new ArrayList<>())
                .location(toLocationDTO(job.getLocation()))
                .salary(toSalaryDTO(job.getSalary()))
                .employmentType(job.getEmploymentType())
                .experienceLevel(job.getExperienceLevel())
                .educationLevel(job.getEducationLevel())
                .vacancies(job.getVacancies())
                .applicationDeadline(toLocalDate(job.getApplicationDeadline()))
                .status(job.getStatus())
                .build();
    }

    // Convert list of Job entities to list of JobPostDTOs
    public List<JobPostDTO> toDTOList(List<Job> jobs) {
        if (jobs == null) {
            return new ArrayList<>();
        }

        return jobs.stream()
                .map(this::toDTO)
                .toList();
    }

    // Update existing Job entity with DTO data
    public void updateEntityFromDTO(JobPostDTO jobPostDTO, Job job) {
        if (jobPostDTO == null || job == null) {
            return;
        }

        if (jobPostDTO.getTitle() != null) {
            job.setTitle(jobPostDTO.getTitle());
        }
        if (jobPostDTO.getDepartment() != null) {
            job.setDepartment(jobPostDTO.getDepartment());
        }
        if (jobPostDTO.getDescription() != null) {
            job.setDescription(jobPostDTO.getDescription());
        }
        if (jobPostDTO.getRequirements() != null) {
            job.setRequirements(jobPostDTO.getRequirements());
        }
        if (jobPostDTO.getResponsibilities() != null) {
            job.setResponsibilities(jobPostDTO.getResponsibilities());
        }
        if (jobPostDTO.getSkills() != null) {
            job.setSkills(new ArrayList<>(jobPostDTO.getSkills()));
        }
        if (jobPostDTO.getLocation() != null) {
            job.setLocation(toLocationEntity(jobPostDTO.getLocation()));
        }
        if (jobPostDTO.getSalary() != null) {
            job.setSalary(toSalaryEntity(jobPostDTO.getSalary()));
        }
        if (jobPostDTO.getEmploymentType() != null) {
            job.setEmploymentType(jobPostDTO.getEmploymentType());
        }
        if (jobPostDTO.getExperienceLevel() != null) {
            job.setExperienceLevel(jobPostDTO.getExperienceLevel());
        }
        if (jobPostDTO.getEducationLevel() != null) {
            job.setEducationLevel(jobPostDTO.getEducationLevel());
        }
        if (jobPostDTO.getVacancies() != null) {
            job.setVacancies(jobPostDTO.getVacancies());
        }
        if (jobPostDTO.getApplicationDeadline() != null) {
            job.setApplicationDeadline(toLocalDateTime(jobPostDTO.getApplicationDeadline()));
        }
        if (jobPostDTO.getStatus() != null) {
            job.setStatus(jobPostDTO.getStatus());
        }
    }

    // Helper methods for Location conversion
    private Job.Location toLocationEntity(JobPostDTO.LocationDTO locationDTO) {
        if (locationDTO == null) {
            return null;
        }

        return Job.Location.builder()
                .city(locationDTO.getCity())
                .state(locationDTO.getState())
                .country(locationDTO.getCountry())
                .remote(locationDTO.isRemote())
                .build();
    }

    private JobPostDTO.LocationDTO toLocationDTO(Job.Location location) {
        if (location == null) {
            return null;
        }

        return JobPostDTO.LocationDTO.builder()
                .city(location.getCity())
                .state(location.getState())
                .country(location.getCountry())
                .remote(location.getRemote())
                .build();
    }

    // Helper methods for Salary conversion
    private Job.Salary toSalaryEntity(JobPostDTO.SalaryDTO salaryDTO) {
        if (salaryDTO == null) {
            return null;
        }

        return Job.Salary.builder()
                .min(parseDouble(salaryDTO.getMin()))
                .max(parseDouble(salaryDTO.getMax()))
                .currency(salaryDTO.getCurrency())
                .period(salaryDTO.getPeriod())
                .build();
    }

    private JobPostDTO.SalaryDTO toSalaryDTO(Job.Salary salary) {
        if (salary == null) {
            return null;
        }

        return JobPostDTO.SalaryDTO.builder()
                .min(toString(salary.getMin()))
                .max(toString(salary.getMax()))
                .currency(salary.getCurrency())
                .period(salary.getPeriod())
                .build();
    }

    // Helper methods for date conversion
    private LocalDateTime toLocalDateTime(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.atTime(LocalTime.MAX); // Set to end of day
    }

    private LocalDate toLocalDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.toLocalDate();
    }

    // Helper methods for salary string/double conversion
    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String toString(Double value) {
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }
    
   
}