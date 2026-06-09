package com.org.bgv.data.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.org.bgv.common.repository.CountryRepository;
import com.org.bgv.common.repository.StateRegionRepository;
import com.org.bgv.company.dto.CompanyType;
import com.org.bgv.onboarding.entity.CompanyAttributeDefinition;
import com.org.bgv.onboarding.repository.CompanyAttributeDefinitionRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CompanyAttributeSeeder {
	
	
	
	private final CompanyAttributeDefinitionRepository definitionRepository;

	     @Bean
	    CommandLineRunner seedCompanyAttribute() {
	        return args -> {

        if (definitionRepository.existsByCompanyType(CompanyType.VENDOR)) {
            return;
        }

        saveDefinition("VENDOR_TYPE",
                "Vendor Type",
                "STRING",
                "DROPDOWN",
                true,
                1);

        saveDefinition("COVERAGE_AREA",
                "Coverage Area",
                "STRING",
                "DROPDOWN",
                true,
                2);

        saveDefinition("DEFAULT_TAT_DAYS",
                "Default TAT (Days)",
                "INTEGER",
                "NUMBER",
                true,
                3);

        saveDefinition("MAX_DAILY_CAPACITY",
                "Maximum Daily Capacity",
                "INTEGER",
                "NUMBER",
                false,
                4);

        saveDefinition("FIELD_VERIFICATION_AVAILABLE",
                "Field Verification Available",
                "BOOLEAN",
                "DROPDOWN",
                true,
                5);

        saveDefinition("API_INTEGRATION_SUPPORTED",
                "API Integration Supported",
                "BOOLEAN",
                "DROPDOWN",
                false,
                6);

        saveDefinition("PAYMENT_TERMS",
                "Payment Terms",
                "STRING",
                "DROPDOWN",
                false,
                7);

        saveDefinition("ASSIGNMENT_MODE",
                "Assignment Mode",
                "STRING",
                "DROPDOWN",
                true,
                8);

        saveDefinition("SLA_COMPLIANCE_PERCENT",
                "SLA Compliance %",
                "DECIMAL",
                "NUMBER",
                false,
                9);

        saveDefinition("QUALITY_SCORE",
                "Quality Score",
                "DECIMAL",
                "NUMBER",
                false,
                10);

        saveDefinition("LANGUAGES_SUPPORTED",
                "Languages Supported",
                "STRING",
                "MULTI_SELECT",
                false,
                11);
	        };
	     
    }

    private void saveDefinition(
            String code,
            String label,
            String dataType,
            String controlType,
            boolean required,
            int displayOrder) {

        CompanyAttributeDefinition definition =CompanyAttributeDefinition.builder()
        		.build();
                

        definition.setCompanyType(CompanyType.VENDOR);
        definition.setAttributeCode(code);
        definition.setAttributeLabel(label);
        definition.setDataType(dataType);
        definition.setControlType(controlType);
        definition.setRequired(required);
        definition.setDisplayOrder(displayOrder);

        definitionRepository.save(definition);
    }
}
