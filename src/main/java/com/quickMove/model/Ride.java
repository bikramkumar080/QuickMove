package com.quickMove.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver; // Assuming the driver is a User

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger; // Assuming the passenger is a User

    private String startLocation;
    private String endLocation;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    @Enumerated(EnumType.STRING)
    private RideStatus rideStatus;

    // Getters and setters

    public enum RideStatus {
        PENDING,
        ONGOING,
        COMPLETED,
        CANCELLED
    }
}

