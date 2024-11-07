package com.quickMove.Service;

import com.quickMove.model.Ride;
import com.quickMove.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    public List<Ride> getRideHistoryByPassenger(Long passengerId) {
        return rideRepository.findByPassengerId(passengerId);
    }

    public List<Ride> getRideHistoryByDriver(Long driverId) {
        return rideRepository.findByDriverId(driverId);
    }

    public boolean cancelRide(Long rideId, String reason, Long userId) {
        Optional<Ride> ride = rideRepository.findById(rideId);
        if (ride.isPresent() && ride.get().getRideStatus() != Ride.RideStatus.CANCELLED && ride.get().getRideStatus() != Ride.RideStatus.COMPLETED && (Objects.equals(ride.get().getPassenger().getId(), userId) || Objects.equals(ride.get().getDriver().getId(), userId))) {
            Ride r = ride.get();
            r.setRideStatus(Ride.RideStatus.CANCELLED);
            rideRepository.save(r);
            return true;
        }
        return false;
    }
}
