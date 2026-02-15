package com.org.bgv.auth.dto;

public record TokenValidationResult(
        boolean valid,
        String message
) {}
