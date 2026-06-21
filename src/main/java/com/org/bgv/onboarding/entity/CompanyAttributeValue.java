package com.org.bgv.onboarding.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(
        name = "company_attribute_values",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "company_id",
                                "attribute_definition_id"
                        }
                )
        }
)
public class CompanyAttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_definition_id")
    private CompanyAttributeDefinition attributeDefinition;

    @Column(length = 500)
    private String attributeValue;
}