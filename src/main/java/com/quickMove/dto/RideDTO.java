package com.quickMove.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RideDTO {
    private Long id;
    private Long driverId;
    private Long passengerId;
    private String startLocation;
    private String endLocation;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String rideStatus;
}
