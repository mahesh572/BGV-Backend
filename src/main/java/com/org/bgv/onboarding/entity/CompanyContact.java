package com.org.bgv.onboarding.entity;

import com.org.bgv.onboarding.dto.ContactType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "company_contacts")
public class CompanyContact {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	

    @ManyToOne
    private Company company;

    private String name;

    private String title;

    private String email;

    private String phone;

    private boolean primaryContact;
    
    @Enumerated(EnumType.STRING)
    private ContactType contactType;
}