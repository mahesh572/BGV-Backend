package com.org.bgv.vendor.builder;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.org.bgv.candidate.entity.EducationHistory;
import com.org.bgv.vendor.dto.ObjectFieldDTO;

@Component
public class EducationFieldBuilder implements ObjectFieldBuilder<EducationHistory> {

    @Override
    public List<ObjectFieldDTO> buildFields(EducationHistory edu) {

        List<ObjectFieldDTO> fields = new ArrayList<>();

        fields.add(FieldsUtil.text(
                "DEGREE",
                "Degree",
                edu.getDegree() != null ? edu.getDegree().getName() : null,
                true
        ));

        fields.add(FieldsUtil.text(
                "FIELD_OF_STUDY",
                "Field of Study",
                edu.getField() != null ? edu.getField().getName() : null,
                true
        ));

        fields.add(FieldsUtil.text(
                "INSTITUTE_NAME",
                "Institute Name",
                edu.getInstitute_name(),
                true
        ));

        fields.add(FieldsUtil.text(
                "UNIVERSITY_NAME",
                "University Name",
                edu.getUniversity_name(),
                false
        ));

        fields.add(FieldsUtil.date(
                "FROM_DATE",
                "From Date",
                edu.getFromDate(),
                true
        ));

        fields.add(FieldsUtil.date(
                "TO_DATE",
                "To Date",
                edu.getToDate(),
                false
        ));

        fields.add(FieldsUtil.number(
                "YEAR_OF_PASSING",
                "Year of Passing",
                edu.getYearOfPassing(),
                true,
                false
        ));

        fields.add(FieldsUtil.text(
                "TYPE_OF_EDUCATION",
                "Type of Education",
                edu.getTypeOfEducation(),
                false
        ));

        fields.add(FieldsUtil.text(
                "GRADE",
                "Grade",
                edu.getGrade(),
                false
        ));

        fields.add(FieldsUtil.number(
                "GPA",
                "GPA",
                edu.getGpa(),
                false,
                false
        ));

        fields.add(FieldsUtil.text(
                "LOCATION",
                "Location",
                buildLocation(edu),
                false
        ));

        return fields;
    }

    private String buildLocation(EducationHistory edu) {
        return String.join(", ",
                nonNull(edu.getCity()),
                nonNull(edu.getState()),
                nonNull(edu.getCountry())
        ).replaceAll(", ,", "").replaceAll(", $", "");
    }

    private String nonNull(String value) {
        return value == null ? "" : value;
    }
}
