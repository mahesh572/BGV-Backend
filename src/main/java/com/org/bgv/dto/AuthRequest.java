package com.org.bgv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.org.bgv.common.navigation.PortalType;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthRequest {
    private String email;
    private String password;
    
    @JsonProperty("portal")
    private PortalType portal;
}
