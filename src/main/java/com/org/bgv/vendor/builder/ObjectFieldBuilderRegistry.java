package com.org.bgv.vendor.builder;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.apache.bcel.generic.ObjectType;
import org.springframework.stereotype.Component;

import com.org.bgv.candidate.entity.EducationHistory;
import com.org.bgv.candidate.entity.IdentityProof;
import com.org.bgv.candidate.entity.WorkExperience;
import com.org.bgv.common.CheckObjectType;
import com.org.bgv.vendor.dto.ObjectFieldDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ObjectFieldBuilderRegistry {

    private final IdentityFieldBuilder identityFieldBuilder;
    private final EducationFieldBuilder educationFieldBuilder;
    private final WorkExperienceFieldBuilder workExperienceFieldBuilder;
    // later: educationFieldBuilder, addressFieldBuilder


    public List<ObjectFieldDTO> resolveFields(
            CheckObjectType objectType,
            Object source
    ) {
        return switch (objectType) {

            case IDENTITY ->
                    identityFieldBuilder.buildFields((IdentityProof) source);

            case EDUCATION ->
                    educationFieldBuilder.buildFields((EducationHistory)source);

            case ADDRESS ->
                    new ArrayList();

            case WORK_EXPERIENCE ->
                    workExperienceFieldBuilder.buildFields((WorkExperience)source);
        };
    }


}
