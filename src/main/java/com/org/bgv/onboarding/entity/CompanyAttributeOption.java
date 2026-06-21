package com.org.bgv.onboarding.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(
        name = "company_attribute_options",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "attribute_definition_id",
                                "option_code"
                        }
                )
        }
)
public class CompanyAttributeOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_definition_id")
    private CompanyAttributeDefinition attributeDefinition;

    private String optionCode;

    private String optionLabel;

    private Integer displayOrder;
}