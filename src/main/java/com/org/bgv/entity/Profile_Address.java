package com.org.bgv.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "profile_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile_Address {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long profile_address_id;
	
	private String address_line1;
	private String city;
	private String state;
	private String country;
	private String zip_code;
	private Boolean cur_residing;
	private LocalDate cur_residing_from;
	private Boolean is_permenet_address;
	
	
	@ManyToOne
	@JoinColumn(name = "profile_id")
	private Profile profile;
	
}
