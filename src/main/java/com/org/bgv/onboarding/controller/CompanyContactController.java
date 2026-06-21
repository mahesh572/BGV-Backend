package com.org.bgv.onboarding.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.onboarding.dto.CompanyContactRequest;
import com.org.bgv.onboarding.dto.CompanyContactResponse;
import com.org.bgv.onboarding.service.CompanyContactService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/companies/{companyId}/contacts")
@RequiredArgsConstructor
@Slf4j
public class CompanyContactController {

    private final CompanyContactService companyContactService;

    @PostMapping
    public ResponseEntity<CustomApiResponse<?>> addContact(
            @PathVariable Long companyId,
            @RequestBody CompanyContactRequest request) {

        try {

            log.info("addContact :: companyId={}, request={}", companyId, request);

            CompanyContactResponse companyContactResponse = companyContactService.addContact(companyId, request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success(
                            "Contact added successfully",
                            companyContactResponse,
                            HttpStatus.CREATED));

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST));

        } catch (Exception e) {

            log.error("Error adding contact", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to add contact",
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    @PutMapping("/{contactId}")
    public ResponseEntity<CustomApiResponse<Boolean>> updateContact(
            @PathVariable Long companyId,
            @PathVariable Long contactId,
            @RequestBody CompanyContactRequest request) {

        try {

            log.info("updateContact :: companyId={}, contactId={}",
                    companyId, contactId);

            companyContactService.updateContact(contactId, request);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Contact updated successfully",
                            true,
                            HttpStatus.OK));

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST));

        } catch (Exception e) {

            log.error("Error updating contact", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to update contact",
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    @DeleteMapping("/{contactId}")
    public ResponseEntity<CustomApiResponse<Boolean>> deleteContact(
            @PathVariable Long companyId,
            @PathVariable Long contactId) {

        try {

            log.info("deleteContact :: companyId={}, contactId={}",
                    companyId, contactId);

            companyContactService.deleteContact(contactId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Contact deleted successfully",
                            true,
                            HttpStatus.OK));

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST));

        } catch (Exception e) {

            log.error("Error deleting contact", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to delete contact",
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    @GetMapping
    public ResponseEntity<CustomApiResponse<List<CompanyContactResponse>>> getContacts(
            @PathVariable Long companyId) {

        try {

            log.info("getContacts :: companyId={}", companyId);

            List<CompanyContactResponse> contacts =
                    companyContactService.getContacts(companyId);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Contacts fetched successfully",
                            contacts,
                            HttpStatus.OK));

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST));

        } catch (Exception e) {

            log.error("Error fetching contacts", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to fetch contacts",
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}