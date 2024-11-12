package com.quickMove.dto;

import com.quickMove.model.Ride;
import com.quickMove.model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RideDTO {
    private Long id;
    private DriverDto driver;
    private UserDTO passenger;
    private Double startLocationLatitude;
    private Double startLocationLongitude;
    private Double endLocationLatitude;
    private Double endLocationLongitude;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String price;
    private Long rideType;
    private String cancellationReason;
}
