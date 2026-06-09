package com.org.bgv.onboarding.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Entity
@Table(name = "company_attribute_options")
public class CompanyAttributeOption {

    @Id
    private Long id;

    @ManyToOne
    private CompanyAttributeDefinition attributeDefinition;

    private String optionCode;

    private String optionLabel;

    private Integer displayOrder;
}
