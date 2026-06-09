package com.org.bgv.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "state_regions")
@Getter
@Setter
public class StateRegion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;     // AP, TS, CA

    private String name;     // Andhra Pradesh, Telangana

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
}
