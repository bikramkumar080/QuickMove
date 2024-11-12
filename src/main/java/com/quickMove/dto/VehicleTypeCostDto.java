package com.quickMove.dto;

import com.quickMove.model.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleTypeCostDto {
    private VehicleType vehicleType;
    private String cost;
}
