package com.org.bgv.company.dto;

import java.util.List;

import lombok.Data;

@Data
public class EmployerPackageConfigurationRequestDTO {

    private Long employerPackageId;
    private List<CategorySelectionDTO> categories;

    @Data
    public static class CategorySelectionDTO {
        private Long categoryId;
        private List<SelectedRuleDTO> selectedRules;
    }

    @Data
    public static class SelectedRuleDTO {
        private Long ruleTypeId;
        private Integer selectedCount;
    }
}
