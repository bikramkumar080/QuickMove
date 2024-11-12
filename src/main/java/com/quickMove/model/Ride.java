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
    @JoinColumn(name = "driver_id", nullable = true)
    private User driver; // Assuming the driver is a User

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger; // Assuming the passenger is a User

    @Enumerated(EnumType.STRING)
    private Status status;

    private Long rideType;

    private Double startLocationLatitude;

    private Double startLocationLongitude;

    private Double endLocationLatitude;

    private Double endLocationLongitude;

    private LocalDateTime startTime;

    private String price;

    private LocalDateTime endTime;

    private String cancellationReason;

    public enum Status {
        OFFERED,
        UNASSIGNED,
        ASSIGNED,
        ONGOING,
        CANCELLED,
        COMPLETED
    }
}

