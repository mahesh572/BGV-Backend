package com.org.bgv.vendor.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.vendor.dto.UpdateFieldComparisonRequest;
import com.org.bgv.vendor.service.FieldComparisonService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vendor/fields")
@RequiredArgsConstructor
public class VerificationFieldController {

    private final FieldComparisonService fieldComparisonService;

    @PutMapping
    public ResponseEntity<CustomApiResponse<String>> updateField(
            @Valid @RequestBody UpdateFieldComparisonRequest request) {

        fieldComparisonService.updateField(request);

        return ResponseEntity.ok(
                CustomApiResponse.success(
                        "Field updated successfully",
                        "SUCCESS",
                        HttpStatus.OK
                )
        );
    }
}
