package com.quickMove.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private String licenseNumber;
    private String vehicleType;
    private String vehicleNumber;
    private String vehicleModel;
    private String vehicleColor;
    private Double latitude;
    private Double longitude;
}
