package com.org.bgv.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasicDetailsDTO {
	
	private Long profileId;
	private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String linkedIn;
    private String nationality;
    private String passportNumber;
   
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate passportExpiry;
    
    private boolean verified;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime verifiedAt;
    private String verifiedBy;
    
    private Long user_id;
    private String verificationStatus;
    private String status;
   
    
    @NotNull(message = "Candidate ID is required")
    private Long candidateId;
}
