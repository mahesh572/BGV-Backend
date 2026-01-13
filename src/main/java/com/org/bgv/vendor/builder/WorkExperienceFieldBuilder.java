package com.org.bgv.vendor.builder;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.org.bgv.candidate.entity.WorkExperience;
import com.org.bgv.vendor.dto.ObjectFieldDTO;

@Component
public class WorkExperienceFieldBuilder
        implements ObjectFieldBuilder<WorkExperience> {

    @Override
    public List<ObjectFieldDTO> buildFields(WorkExperience work) {

        List<ObjectFieldDTO> fields = new ArrayList<>();

        fields.add(FieldsUtil.text(
                "COMPANY_NAME",
                "Company Name",
                work.getCompany_name(),
                true
        ));

        fields.add(FieldsUtil.text(
                "POSITION",
                "Position / Designation",
                work.getPosition(),
                true
        ));

        fields.add(FieldsUtil.date(
                "START_DATE",
                "Start Date",
                work.getStart_date(),
                true
        ));

        fields.add(FieldsUtil.date(
                "END_DATE",
                "End Date",
                work.getCurrentlyWorking() ? null : work.getEnd_date(),
                false
        ));

        fields.add(FieldsUtil.bool(
                "CURRENTLY_WORKING",
                "Currently Working",
                work.getCurrentlyWorking(),
                false
        ));

        fields.add(FieldsUtil.number(
                "DURATION_MONTHS",
                "Duration (Months)",
                work.getDurationInMonths(),
                false,
                true
        ));

        fields.add(FieldsUtil.number(
                "DURATION_YEARS",
                "Duration (Years)",
                work.getDurationInYears(),
                false,
                true
        ));

        fields.add(FieldsUtil.text(
                "EMPLOYMENT_TYPE",
                "Employment Type",
                work.getEmploymentType(),
                false
        ));

        fields.add(FieldsUtil.text(
                "NOTICE_PERIOD",
                "Notice Period",
                work.getNoticePeriod(),
                false
        ));

        fields.add(FieldsUtil.text(
                "EMPLOYEE_ID",
                "Employee ID",
                work.getEmployee_id(),
                false
        ));

        fields.add(FieldsUtil.text(
                "MANAGER_EMAIL",
                "Manager Email",
                work.getManager_email_id(),
                false
        ));

        fields.add(FieldsUtil.text(
                "HR_EMAIL",
                "HR Email",
                work.getHr_email_id(),
                false
        ));

        fields.add(FieldsUtil.text(
                "REASON_FOR_LEAVING",
                "Reason for Leaving",
                work.getReason(),
                false
        ));

        fields.add(FieldsUtil.text(
                "WORK_LOCATION",
                "Work Location",
                buildLocation(work),
                false
        ));

        return fields;
    }

    private String buildLocation(WorkExperience work) {
        return String.join(", ",
                safe(work.getCity()),
                safe(work.getState()),
                safe(work.getCountry())
        ).replaceAll(", ,", "").replaceAll(", $", "");
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
