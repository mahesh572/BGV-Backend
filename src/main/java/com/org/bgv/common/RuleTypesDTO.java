package com.org.bgv.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore unknown properties in request
public class RuleTypesDTO {
    
    @JsonProperty("ruleTypeId")
    @JsonAlias({"id", "rule_type_id"}) // Accept multiple input names
    private Long ruleTypeId;
    
    @NotNull(message = "Category ID is required")
    @JsonProperty("categoryId")
    @JsonAlias({"category_id", "checkCategoryId"})
    private Long categoryId;
    
    @NotBlank(message = "Name is required")
    @JsonProperty("name")
    @JsonAlias({"ruleName", "rule_name"})
    private String name;
    
    @NotBlank(message = "Code is required")
    @JsonProperty("code")
    @JsonAlias({"ruleCode", "rule_code"})
    private String code;
    
    @JsonProperty("label")
    @JsonAlias({"ruleLabel", "rule_label"})
    private String label;
    
    // For displaying category name in responses
    @JsonProperty("categoryName")
    @JsonAlias({"category_name", "checkCategoryName"})
    private String categoryName;
    
    private Long minCount;
    private Long maxCount;
}