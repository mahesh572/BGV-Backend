package com.org.bgv.vendor.dto;


import lombok.Data;

@Data
public class CompleteFieldVisitRequest {

    private String outcome;

    private String remarks;

    private Double latitude;

    private Double longitude;

    private String address;
}
