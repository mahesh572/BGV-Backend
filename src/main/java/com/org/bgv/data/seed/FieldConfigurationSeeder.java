package com.org.bgv.data.seed;

import com.org.bgv.common.entity.FieldConfiguration;
import com.org.bgv.common.repository.FieldConfigurationRepository;
import com.org.bgv.dto.CheckCategoryEnum;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FieldConfigurationSeeder implements CommandLineRunner {
	
	 @Value("${app.seed.enabled:false}")
	    private boolean seedEnabled;

    private final FieldConfigurationRepository repository;

    @Override
    public void run(String... args) {
    	
    	
    	if (!seedEnabled) {
            System.out.println("Database seeding is disabled.");
            return;
        }

        if (repository.count() > 0) {
            return;
        }

        List<FieldConfiguration> fields = List.of(

                // =========================================
                // EDUCATION
                // =========================================

                create(CheckCategoryEnum.EDUCATION, "degree",
                        "Degree", "STRING",
                        true, true, true, true, 1),

                create(CheckCategoryEnum.EDUCATION, "field",
                        "Field Of Study", "STRING",
                        true, true, true, true, 2),

                create(CheckCategoryEnum.EDUCATION, "instituteName",
                        "Institute Name", "STRING",
                        true, true, true, true, 3),

                create(CheckCategoryEnum.EDUCATION, "universityName",
                        "University Name", "STRING",
                        true, true, true, true, 4),

                create(CheckCategoryEnum.EDUCATION, "fromDate",
                        "From Date", "DATE",
                        true, true, true, true, 5),

                create(CheckCategoryEnum.EDUCATION, "toDate",
                        "To Date", "DATE",
                        true, true, true, true, 6),

                create(CheckCategoryEnum.EDUCATION, "yearOfPassing",
                        "Year Of Passing", "NUMBER",
                        true, true, true, true, 7),

                create(CheckCategoryEnum.EDUCATION, "grade",
                        "Grade", "STRING",
                        false, true, true, true, 8),

                create(CheckCategoryEnum.EDUCATION, "gpa",
                        "GPA", "NUMBER",
                        false, true, true, true, 9),

                create(CheckCategoryEnum.EDUCATION, "city",
                        "City", "STRING",
                        false, true, true, true, 10),

                create(CheckCategoryEnum.EDUCATION, "state",
                        "State", "STRING",
                        false, true, true, true, 11),

                create(CheckCategoryEnum.EDUCATION, "country",
                        "Country", "STRING",
                        false, true, true, true, 12),

                create(CheckCategoryEnum.EDUCATION, "typeOfEducation",
                        "Education Type", "STRING",
                        false, true, true, true, 13),

                // =========================================
                // WORK EXPERIENCE
                // =========================================

                create(CheckCategoryEnum.WORK_EXPERIENCE, "companyName",
                        "Company Name", "STRING",
                        true, true, true, true, 1),

                create(CheckCategoryEnum.WORK_EXPERIENCE, "position",
                        "Designation", "STRING",
                        true, true, true, true, 2),

                create(CheckCategoryEnum.WORK_EXPERIENCE, "employeeId",
                        "Employee Id", "STRING",
                        false, true, true, true, 3),

                create(CheckCategoryEnum.WORK_EXPERIENCE, "startDate",
                        "Start Date", "DATE",
                        true, true, true, true, 4),

                create(CheckCategoryEnum.WORK_EXPERIENCE, "endDate",
                        "End Date", "DATE",
                        false, true, true, true, 5),

                create(CheckCategoryEnum.WORK_EXPERIENCE, "employmentType",
                        "Employment Type", "STRING",
                        false, true, true, true, 6),

                create(CheckCategoryEnum.WORK_EXPERIENCE, "currentlyWorking",
                        "Currently Working", "BOOLEAN",
                        false, false, true, false, 7),

                create(CheckCategoryEnum.WORK_EXPERIENCE, "managerEmailId",
                        "Manager Email", "STRING",
                        false, false, true, false, 8),

                create(CheckCategoryEnum.WORK_EXPERIENCE, "hrEmailId",
                        "HR Email", "STRING",
                        false, false, true, false, 9),

                create(CheckCategoryEnum.WORK_EXPERIENCE, "address",
                        "Address", "STRING",
                        false, true, true, true, 10),

                create(CheckCategoryEnum.WORK_EXPERIENCE, "city",
                        "City", "STRING",
                        false, true, true, true, 11),

                create(CheckCategoryEnum.WORK_EXPERIENCE, "state",
                        "State", "STRING",
                        false, true, true, true, 12),

                create(CheckCategoryEnum.WORK_EXPERIENCE, "country",
                        "Country", "STRING",
                        false, true, true, true, 13),

                create(CheckCategoryEnum.WORK_EXPERIENCE, "noticePeriod",
                        "Notice Period", "STRING",
                        false, false, true, false, 14),

                create(CheckCategoryEnum.WORK_EXPERIENCE, "reason",
                        "Reason For Leaving", "STRING",
                        false, false, true, false, 15),

                // =========================================
                // IDENTITY
                // =========================================

                create(CheckCategoryEnum.IDENTITY, "documentNumber",
                        "Document Number", "STRING",
                        true, true, true, true, 1),

                create(CheckCategoryEnum.IDENTITY, "docTypeId",
                        "Document Type", "STRING",
                        true, true, true, true, 2),

                create(CheckCategoryEnum.IDENTITY, "issueDate",
                        "Issue Date", "DATE",
                        false, true, true, true, 3),

                create(CheckCategoryEnum.IDENTITY, "expiryDate",
                        "Expiry Date", "DATE",
                        false, true, true, true, 4)
        );
        repository.saveAll(fields);
        System.out.println("Field Configuration Seeded Successfully.");
    }

    
    private FieldConfiguration create(
            CheckCategoryEnum checkType,
            String fieldName,
            String displayName,
            String dataType,
            boolean mandatory,
            boolean comparable,
            boolean visible,
            boolean reportable,
            int sequenceNo) {

        return FieldConfiguration.builder()
                .checkType(checkType)
                .fieldName(fieldName)
                .displayName(displayName)
                .dataType(dataType)
                .mandatory(mandatory)
                .comparable(comparable)
                .visible(visible)
                .reportable(reportable)
                .sequenceNo(sequenceNo)
                .build();
    }
}