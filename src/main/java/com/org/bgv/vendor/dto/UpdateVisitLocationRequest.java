package com.org.bgv.vendor.dto;


import lombok.Data;

@Data
public class UpdateVisitLocationRequest {

    private Double latitude;

    private Double longitude;

    // Reverse geocoded address
    private String address;

    // GPS accuracy in meters
    private Double accuracy;

    // GPS / MANUAL / HYBRID
    private String source;

    // START / DURING / END
  //  private VisitLocationType visitType;

    // Optional remarks
    private String notes;
}