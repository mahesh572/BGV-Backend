package com.org.bgv.vendor.entity;

import java.util.ArrayList;
import java.util.List;

import com.org.bgv.common.entity.StateRegion;
import com.org.bgv.entity.User;
import com.org.bgv.onboarding.entity.Company;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vendor_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    private String operationalRole;

    private Integer experienceYears;

    private Boolean fieldVerificationAvailable;

    private Boolean vehicleAvailable;

    private Integer maxDailyCapacity;

    @Enumerated(EnumType.STRING)
  //  private AvailabilityStatus availabilityStatus;

    @ManyToMany
    @JoinTable(
            name = "vendor_user_regions",
            joinColumns = @JoinColumn(name = "vendor_user_id"),
            inverseJoinColumns = @JoinColumn(name = "state_region_id")
    )
    private List<StateRegion> regions = new ArrayList();

}
