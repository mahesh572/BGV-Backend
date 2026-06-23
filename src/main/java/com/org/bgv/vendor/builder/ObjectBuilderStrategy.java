package com.org.bgv.vendor.builder;

import java.util.List;

import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.vendor.dto.ObjectDTO;

public interface ObjectBuilderStrategy {

    CheckCategoryEnum supportedCategory();

    List<ObjectDTO> buildObjects(
            VerificationCaseCheck check);
}
