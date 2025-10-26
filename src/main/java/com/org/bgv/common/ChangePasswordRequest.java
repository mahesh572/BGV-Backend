package com.org.bgv.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Change password request")
public class ChangePasswordRequest {
    
    
    private String currentPassword;
    
    @Schema(description = "New password", example = "newPassword123", required = true)
    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "New password must be at least 6 characters long")
    private String newPassword;
    
    @Schema(description = "Confirm new password", example = "newPassword123", required = true)
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}