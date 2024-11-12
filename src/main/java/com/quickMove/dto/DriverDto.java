package com.quickMove.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class DriverDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String licenseNumber;
    private String vehicleNumber;
    private String vehicleModel;
    private String vehicleColor;
    private Long organizationId;
    private Long vehicleTypeId;
    private String vehicleType;
    private Integer capacity;
}
