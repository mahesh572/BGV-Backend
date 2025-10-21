package com.org.bgv.common;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User role update request")
public class UserRoleUpdateRequest {
    
    @Schema(description = "List of role IDs to assign to the user", example = "[1, 2, 3]")
    private List<Long> roleIdsToAdd;
    
    @Schema(description = "List of role IDs to remove from the user", example = "[4, 5]")
    private List<Long> roleIdsToRemove;
}
