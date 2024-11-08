package com.quickMove.repository;

import com.quickMove.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByPassengerId(Long passengerId);
    List<Ride> findByDriverId(Long driverId);
    List<Ride> findByRideStatus(Ride.RideStatus rideStatus);
    List<Ride> findByPassengerIdAndRideStatusIn(Long passengerId, List<String> rideStatus);
    List<Ride> findByDriverIdAndRideStatusIn(Long passengerId, List<String> rideStatus);
}
