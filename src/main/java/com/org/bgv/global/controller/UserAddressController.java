package com.org.bgv.global.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.global.dto.UserAddressDTO;
import com.org.bgv.global.service.UserAddressService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user/{userId}/addresses")
@RequiredArgsConstructor
@Slf4j
public class UserAddressController {

    private final UserAddressService userAddressService;

    // =====================================================
    // CREATE
    // =====================================================
    @PostMapping
    public ResponseEntity<CustomApiResponse<List<UserAddressDTO>>> saveUserAddresses(
            @PathVariable Long userId,
            @RequestBody List<UserAddressDTO> addressDTOs
    ) {
        log.info("Saving user addresses: {}", addressDTOs);

        try {
            List<UserAddressDTO> saved =
                    userAddressService.updateUserAddresses(addressDTOs, userId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success(
                            "Addresses saved successfully",
                            saved,
                            HttpStatus.CREATED
                    ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to save addresses",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // =====================================================
    // READ
    // =====================================================
    @GetMapping
    public ResponseEntity<CustomApiResponse<List<UserAddressDTO>>> getUserAddresses(
            @PathVariable Long userId
    ) {
        try {
            List<UserAddressDTO> addresses =
                    userAddressService.getAddressesByUser(userId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Addresses retrieved successfully",
                            addresses,
                            HttpStatus.OK
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to retrieve addresses",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // =====================================================
    // UPDATE (BULK)
    // =====================================================
    @PutMapping
    public ResponseEntity<CustomApiResponse<List<UserAddressDTO>>> updateUserAddresses(
            @PathVariable Long userId,
            @RequestBody List<UserAddressDTO> addressDTOs
    ) {
        log.info("Updating user addresses: {}", addressDTOs);

        try {
            List<UserAddressDTO> updated =
                    userAddressService.updateUserAddresses(addressDTOs, userId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Addresses updated successfully",
                            updated,
                            HttpStatus.OK
                    )
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to update addresses",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

    // =====================================================
    // DELETE
    // =====================================================
    @DeleteMapping("/{addressId}")
    public ResponseEntity<CustomApiResponse<String>> deleteUserAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId
    ) {
        try {
            userAddressService.deleteAddress(addressId, userId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Address deleted successfully",
                            "Address with ID " + addressId + " deleted",
                            HttpStatus.OK
                    )
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to delete address",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
}

