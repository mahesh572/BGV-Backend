package com.org.bgv.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "degree_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DegreeType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long degreeId;

    private String name;
    private String description;

    // getters and setters
    
    private String label; 
}