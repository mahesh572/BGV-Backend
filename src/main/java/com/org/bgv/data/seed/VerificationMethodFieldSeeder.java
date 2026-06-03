package com.org.bgv.data.seed;

import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.org.bgv.vendor.entity.VerificationMethod;
import com.org.bgv.vendor.entity.VerificationMethodField;
import com.org.bgv.vendor.repository.VerificationMethodFieldRepository;
import com.org.bgv.vendor.repository.VerificationMethodRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class VerificationMethodFieldSeeder implements CommandLineRunner {

    private final VerificationMethodRepository methodRepository;
    private final VerificationMethodFieldRepository fieldRepository;

    @Override
    public void run(String... args) {

        if (fieldRepository.count() > 0) {
            return;
        }

        seedEmailFields();
        seedPhoneFields();
        seedPortalFields();
        seedPhysicalVisitFields();
        seedDatabaseFields();
        seedVideoCallFields();
        seedOtherFields();
    }

    private void seedEmailFields() {

        
        Optional<VerificationMethod> optionalMethod =
                methodRepository.findByCode("EMAIL");
                       
        
        if (optionalMethod.isEmpty()) {
            return;
        }

        VerificationMethod method = optionalMethod.get();

        save(method, "contactName",
                "Contact Name",
                "TEXT",
                true,
                1,
                "Enter HR contact name");

        save(method, "designation",
                "Designation",
                "TEXT",
                false,
                2,
                "HR Executive");

        save(method, "email",
                "Email Address",
                "EMAIL",
                true,
                3,
                "hr@company.com");

        save(method, "companyName",
                "Company Name",
                "TEXT",
                true,
                4,
                "ABC Technologies Pvt Ltd");

        save(method, "remarks",
                "Remarks",
                "TEXTAREA",
                false,
                5,
                "Additional notes");
    }

    private void seedPhoneFields() {

        
        Optional<VerificationMethod> optionalMethod =
                methodRepository.findByCode("PHONE");
                       
        
        if (optionalMethod.isEmpty()) {
            return;
        }

        VerificationMethod method = optionalMethod.get();

        save(method, "contactName",
                "Contact Name",
                "TEXT",
                true,
                1,
                "Enter contact name");

        save(method, "phoneNumber",
                "Phone Number",
                "PHONE",
                true,
                2,
                "+91 XXXXX XXXXX");

        save(method, "designation",
                "Designation",
                "TEXT",
                false,
                3,
                "Manager");

        save(method, "organization",
                "Organization",
                "TEXT",
                true,
                4,
                "Company Name");

        save(method, "remarks",
                "Call Notes",
                "TEXTAREA",
                false,
                5,
                "Conversation summary");
    }

    private void seedPortalFields() {

        
        Optional<VerificationMethod> optionalMethod =
                methodRepository.findByCode("PORTAL");
                       
        
        if (optionalMethod.isEmpty()) {
            return;
        }

        VerificationMethod method = optionalMethod.get();

        save(method, "portalUrl",
                "Portal URL",
                "URL",
                true,
                1,
                "https://portal.company.com");

        save(method, "username",
                "Username",
                "TEXT",
                true,
                2,
                "Enter username");

        save(method, "remarks",
                "Verification Notes",
                "TEXTAREA",
                false,
                3,
                "Portal verification notes");
    }

    private void seedPhysicalVisitFields() {

              

        
        Optional<VerificationMethod> optionalMethod =
                methodRepository.findByCode("PHYSICAL_VISIT");
                       
        
        if (optionalMethod.isEmpty()) {
            return;
        }

        VerificationMethod method = optionalMethod.get();

        save(method, "contactName",
                "Contact Person",
                "TEXT",
                true,
                1,
                "Person met");

        save(method, "visitAddress",
                "Visit Address",
                "TEXTAREA",
                true,
                2,
                "Location visited");

        save(method, "visitDate",
                "Visit Date",
                "DATE",
                true,
                3,
                "");

        save(method, "remarks",
                "Visit Notes",
                "TEXTAREA",
                false,
                4,
                "Field visit observations");
    }

    private void seedDatabaseFields() {

    	Optional<VerificationMethod> optionalMethod =
                methodRepository.findByCode("DATABASE");
                       
        
        if (optionalMethod.isEmpty()) {
            return;
        }

        VerificationMethod method = optionalMethod.get();

        save(method, "databaseSource",
                "Database Source",
                "TEXT",
                true,
                1,
                "Court DB / Credit Bureau");

        save(method, "referenceNumber",
                "Reference Number",
                "TEXT",
                false,
                2,
                "Search reference");

        save(method, "remarks",
                "Notes",
                "TEXTAREA",
                false,
                3,
                "Database findings");
    }

    private void seedVideoCallFields() {

        
        
        Optional<VerificationMethod> optionalMethod =
                methodRepository.findByCode("VIDEO_CALL");
                       
        
        if (optionalMethod.isEmpty()) {
            return;
        }

        VerificationMethod method = optionalMethod.get();

        save(method, "participantName",
                "Participant Name",
                "TEXT",
                true,
                1,
                "Candidate Name");

        save(method, "meetingLink",
                "Meeting Link",
                "URL",
                false,
                2,
                "Teams / Zoom URL");

        save(method, "remarks",
                "Video Verification Notes",
                "TEXTAREA",
                false,
                3,
                "Verification observations");
    }

    private void seedOtherFields() {

             

        
        Optional<VerificationMethod> optionalMethod =
                methodRepository.findByCode("OTHER");
                       
        
        if (optionalMethod.isEmpty()) {
            return;
        }

        VerificationMethod method = optionalMethod.get();

        save(method, "methodName",
                "Method Name",
                "TEXT",
                true,
                1,
                "Enter method name");

        save(method, "details",
                "Details",
                "TEXTAREA",
                true,
                2,
                "Describe verification process");
    }

    private void save(
            VerificationMethod method,
            String fieldName,
            String label,
            String type,
            boolean required,
            int order,
            String placeholder) {

        VerificationMethodField field =
                new VerificationMethodField();

        field.setVerificationMethod(method);
        field.setFieldName(fieldName);
        field.setFieldLabel(label);
        field.setFieldType(type);
        field.setRequiredField(required);
        field.setDisplayOrder(order);
        field.setPlaceholder(placeholder);

        fieldRepository.save(field);
    }
}
