package com.org.bgv.entity;


import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "check_category")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CheckCategory {
   
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    private String name;
    private String description;
    private String label;
    private String code;
    private Boolean hasDocuments;
    private Boolean isActive;
    private Double price;
    
 // Add SLA configuration
    @Column(name = "sla_days")
    private Integer slaDays = 14;
    
    @Column(name = "warning_threshold_days")
    private Integer warningThresholdDays = 3;
    

    // getters and setters
}