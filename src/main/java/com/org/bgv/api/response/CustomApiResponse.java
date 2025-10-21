package com.org.bgv.api.response;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private int statusCode;

    public static <T> CustomApiResponse<T> success(String message, T data, HttpStatus status) {
        return new CustomApiResponse<>(true, message, data, status.value());
    }

    public static <T> CustomApiResponse<T> failure(String message, HttpStatus status) {
        return new CustomApiResponse<>(false, message, null, status.value());
    }
}
