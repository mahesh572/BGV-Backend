package com.org.bgv.data.seed;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.org.bgv.company.dto.CompanyType;
import com.org.bgv.onboarding.entity.CompanyAttributeDefinition;
import com.org.bgv.onboarding.repository.CompanyAttributeDefinitionRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CompanyAttributeSeeder {
	
	 @Value("${app.seed.enabled:false}")
	    private boolean seedEnabled;

    private final CompanyAttributeDefinitionRepository definitionRepository;

    @Bean
    CommandLineRunner seedCompanyAttributes() {

        return args -> {

            seedVendorAttributes();

            seedEmployerAttributes();

            seedUniversityAttributes();
        };
    }

    // ================= VENDOR =================

    private void seedVendorAttributes() {
/*
        saveOrUpdateDefinition(
                CompanyType.VENDOR,
                "VENDOR_TYPE",
                "Vendor Type",
                "STRING",
                "DROPDOWN",
                true,
                1);
*/
        saveOrUpdateDefinition(
                CompanyType.VENDOR,
                "COVERAGE_AREA",
                "Coverage Area",
                "STRING",
                "DROPDOWN",
                true,
                2);

        saveOrUpdateDefinition(
                CompanyType.VENDOR,
                "DEFAULT_TAT_DAYS",
                "Default TAT (Days)",
                "INTEGER",
                "NUMBER",
                true,
                3);

        saveOrUpdateDefinition(
                CompanyType.VENDOR,
                "MAX_DAILY_CAPACITY",
                "Maximum Daily Capacity",
                "INTEGER",
                "NUMBER",
                false,
                4);

        saveOrUpdateDefinition(
                CompanyType.VENDOR,
                "FIELD_VERIFICATION_AVAILABLE",
                "Field Verification Available",
                "BOOLEAN",
                "DROPDOWN",
                true,
                5);

        saveOrUpdateDefinition(
                CompanyType.VENDOR,
                "API_INTEGRATION_SUPPORTED",
                "API Integration Supported",
                "BOOLEAN",
                "DROPDOWN",
                false,
                6);

        saveOrUpdateDefinition(
                CompanyType.VENDOR,
                "PAYMENT_TERMS",
                "Payment Terms",
                "STRING",
                "TEXT",
                false,
                7);

        saveOrUpdateDefinition(
                CompanyType.VENDOR,
                "ASSIGNMENT_MODE",
                "Assignment Mode",
                "STRING",
                "DROPDOWN",
                true,
                8);

        saveOrUpdateDefinition(
                CompanyType.VENDOR,
                "SLA_COMPLIANCE_PERCENT",
                "SLA Compliance %",
                "DECIMAL",
                "NUMBER",
                false,
                9);

        saveOrUpdateDefinition(
                CompanyType.VENDOR,
                "QUALITY_SCORE",
                "Quality Score",
                "DECIMAL",
                "NUMBER",
                false,
                10);

        saveOrUpdateDefinition(
                CompanyType.VENDOR,
                "LANGUAGES_SUPPORTED",
                "Languages Supported",
                "STRING",
                "MULTI_SELECT",
                false,
                11);
    }

    // ================= EMPLOYER =================

    private void seedEmployerAttributes() {

        saveOrUpdateDefinition(
                CompanyType.EMPLOYER,
                "EMPLOYEE_COUNT",
                "Employee Count",
                "INTEGER",
                "NUMBER",
                true,
                1);

        saveOrUpdateDefinition(
                CompanyType.EMPLOYER,
                "HRMS_SYSTEM",
                "HRMS System",
                "STRING",
                "TEXT",
                false,
                2);

        saveOrUpdateDefinition(
                CompanyType.EMPLOYER,
                "PAYROLL_PROVIDER",
                "Payroll Provider",
                "STRING",
                "TEXT",
                false,
                3);

        saveOrUpdateDefinition(
                CompanyType.EMPLOYER,
                "BGV_VOLUME_PER_MONTH",
                "BGV Volume Per Month",
                "INTEGER",
                "NUMBER",
                false,
                4);

        saveOrUpdateDefinition(
                CompanyType.EMPLOYER,
                "WORK_MODE",
                "Work Mode",
                "STRING",
                "DROPDOWN",
                false,
                5);

        saveOrUpdateDefinition(
                CompanyType.EMPLOYER,
                "PAYROLL_FREQUENCY",
                "Payroll Frequency",
                "STRING",
                "DROPDOWN",
                false,
                6);

        saveOrUpdateDefinition(
                CompanyType.EMPLOYER,
                "CONTRACT_EMPLOYEES_SUPPORTED",
                "Contract Employees Supported",
                "BOOLEAN",
                "DROPDOWN",
                false,
                7);
    }

    // ================= UNIVERSITY =================

    private void seedUniversityAttributes() {

        saveOrUpdateDefinition(
                CompanyType.UNIVERSITY,
                "UNIVERSITY_TYPE",
                "University Type",
                "STRING",
                "DROPDOWN",
                true,
                1);

        saveOrUpdateDefinition(
                CompanyType.UNIVERSITY,
                "ACCREDITATION_BODY",
                "Accreditation Body",
                "STRING",
                "TEXT",
                false,
                2);

        saveOrUpdateDefinition(
                CompanyType.UNIVERSITY,
                "GRADING_SYSTEM",
                "Grading System",
                "STRING",
                "DROPDOWN",
                false,
                3);

        saveOrUpdateDefinition(
                CompanyType.UNIVERSITY,
                "DIGITAL_VERIFICATION_SUPPORTED",
                "Digital Verification Supported",
                "BOOLEAN",
                "DROPDOWN",
                false,
                4);

        saveOrUpdateDefinition(
                CompanyType.UNIVERSITY,
                "TRANSCRIPT_DELIVERY_MODE",
                "Transcript Delivery Mode",
                "STRING",
                "DROPDOWN",
                false,
                5);

        saveOrUpdateDefinition(
                CompanyType.UNIVERSITY,
                "AUTONOMOUS_UNIVERSITY",
                "Autonomous University",
                "BOOLEAN",
                "DROPDOWN",
                false,
                6);
    }

    // ================= COMMON METHOD =================

    private void saveOrUpdateDefinition(
            CompanyType companyType,
            String code,
            String label,
            String dataType,
            String controlType,
            boolean required,
            int displayOrder) {

        CompanyAttributeDefinition definition =
                definitionRepository
                        .findByCompanyTypeAndAttributeCode(companyType, code)
                        .orElse(new CompanyAttributeDefinition());

        definition.setCompanyType(companyType);
        definition.setAttributeCode(code);
        definition.setAttributeLabel(label);
        definition.setDataType(dataType);
        definition.setControlType(controlType);
        definition.setRequired(required);
        definition.setDisplayOrder(displayOrder);

        definitionRepository.save(definition);
    }
}