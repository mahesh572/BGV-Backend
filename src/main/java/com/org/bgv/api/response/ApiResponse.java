package com.org.bgv.api.response;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private int statusCode;

    public static <T> ApiResponse<T> success(String message, T data, HttpStatus status) {
        return new ApiResponse<>(true, message, data, status.value());
    }

    public static <T> ApiResponse<T> failure(String message, HttpStatus status) {
        return new ApiResponse<>(false, message, null, status.value());
    }
}
