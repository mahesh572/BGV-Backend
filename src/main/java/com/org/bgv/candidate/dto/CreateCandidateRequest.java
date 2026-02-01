package com.org.bgv.candidate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCandidateRequest {

    // ---- CONTEXT ----
   // @NotNull(message = "Organization id is required")
    private Long organizationId;   // Employer organization // optional
    
   // @NotNull(message = "Organization id is required")
    private Long companyId;

    // ---- BASIC IDENTITY (EMPLOYER PROVIDED) ----
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    // ---- OPTIONAL (EMPLOYER METADATA) ----
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobileNo;

    private String alternateEmail;

    private String employeeId; // employer internal reference

    // ---- CONTROL FLAGS ----
    private boolean sendInvite = true; // Save Draft vs Send Invite
}
