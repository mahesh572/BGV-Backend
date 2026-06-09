package com.org.bgv.onboarding.entity;

import com.org.bgv.entity.Company;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "company_attribute_values")
public class CompanyAttributeValue {

    @Id
    private Long id;

    @ManyToOne
    private Company company;

    @ManyToOne
    private CompanyAttributeDefinition attributeDefinition;

    private String attributeValue;
}