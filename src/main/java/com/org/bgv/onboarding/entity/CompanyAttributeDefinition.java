package com.org.bgv.onboarding.entity;

import com.org.bgv.company.dto.CompanyType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(
        name = "company_attribute_definitions",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "company_type",
                                "attribute_code"
                        }
                )
        }
)
public class CompanyAttributeDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CompanyType companyType;

    private String attributeCode;

    private String attributeName;
    
    private String attributeLabel;

    private String dataType;

    private String controlType;

    private Boolean required;

    private Integer displayOrder;
}
