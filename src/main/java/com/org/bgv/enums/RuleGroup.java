package com.org.bgv.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.org.bgv.dto.CheckCategoryEnum;

public enum RuleGroup {

	RULE(Set.of(CheckCategoryEnum.IDENTITY.getName())),
    DOCUMENT_SELECTION(Set.of(CheckCategoryEnum.IDENTITY.getName())),
    RECORD_COUNT(Set.of( CheckCategoryEnum.EMPLOYMENT.getName())),
    ADD_ON(Set.of( CheckCategoryEnum.EMPLOYMENT.getName())),
    CUSTOM(Set.of(  CheckCategoryEnum.EMPLOYMENT.getName())),
	NONE(Set.of(CheckCategoryEnum.IDENTITY.getName(), CheckCategoryEnum.EDUCATION.getName(), CheckCategoryEnum.EMPLOYMENT.getName())),
	ALL(Set.of( CheckCategoryEnum.EDUCATION.getName(), CheckCategoryEnum.EMPLOYMENT.getName())),
	// 🔹 Education Record Count Selection
    EDUCATION_RECORD_SELECTION(Set.of( CheckCategoryEnum.EDUCATION.getName())),

    // 🔹 Education Add-On Increment
    EDUCATION_ADD_ON(Set.of( CheckCategoryEnum.EDUCATION.getName())),
	
	 // 🔹 Address History Duration
    ADDRESS_DURATION_SELECTION(Set.of( CheckCategoryEnum.ADDRESS.getName())),  // 5 years address
 // 🔹 Address Type Selection
    ADDRESS_TYPE_SELECTION(Set.of( CheckCategoryEnum.ADDRESS.getName())),
    
    ADDRESS_RECORD_COUNT(Set.of( CheckCategoryEnum.ADDRESS.getName()));

    private final Set<String> allowedCategories;

    RuleGroup(Set<String> allowedCategories) {
        this.allowedCategories = allowedCategories;
    }

    public boolean isAllowedFor(String categoryCode) {
        return allowedCategories.contains(categoryCode);
    }

    public static List<RuleGroup> getByCategory(String categoryCode) {
        return Arrays.stream(values())
                .filter(group -> group.isAllowedFor(categoryCode))
                .toList();
    }
}


