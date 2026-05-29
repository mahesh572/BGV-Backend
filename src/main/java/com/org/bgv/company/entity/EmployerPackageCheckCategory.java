package com.org.bgv.company.entity;

import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.EmployerPackage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employer_package_check_category")
public class EmployerPackageCheckCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 🔹 Link to employer package
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_package_id", nullable = false)
    private EmployerPackage employerPackage;

    // 🔹 Category reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_category_id", nullable = false)
    private CheckCategory category;

    // 🔹 Copy from package (optional JSON config)
    @Column(name = "rules_data")
    private String rulesData;

    // 🔹 Optional flags
    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "required")
    private Boolean required;
}