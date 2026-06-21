package com.org.bgv.onboarding.entity;

import com.org.bgv.common.entity.Country;
import com.org.bgv.common.entity.StateRegion;
import com.org.bgv.entity.AddressType;
import com.org.bgv.enums.CompanyAddressType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "company_addresses")
public class CompanyAddress {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Company company;

    private String addressLine1;

    private String addressLine2;
    

    private String city;

    @ManyToOne
    private StateRegion state;

    @ManyToOne
    private Country country;

    private String zipCode;

    @Enumerated(EnumType.STRING)
    private CompanyAddressType addressType;
    
    private Boolean primaryAddress;
}
