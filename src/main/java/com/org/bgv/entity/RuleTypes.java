package com.org.bgv.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
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

@Entity
@Table(name = "rule_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleTypes {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleTypeId;
	
	 @ManyToOne
	 @JoinColumn(name = "check_type_id")
	 private CheckCategory category;
	 
	 private String name;
	 private String code;
	 private String label;
	 private Integer minCount;
	 private Integer maxCount;
	
}
