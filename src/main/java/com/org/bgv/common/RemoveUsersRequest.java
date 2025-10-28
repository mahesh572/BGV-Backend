package com.org.bgv.common;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RemoveUsersRequest {
    
    @NotNull(message = "User IDs cannot be null")
    @NotEmpty(message = "User IDs cannot be empty")
    private List<Long> userIds;
    
    private Long companyId;
}