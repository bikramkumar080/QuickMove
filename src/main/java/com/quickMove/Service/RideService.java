package com.quickMove.Service;

import com.quickMove.model.Ride;
import com.quickMove.model.User;
import com.quickMove.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.quickMove.dto.RideDTO;
import com.quickMove.dto.UserDTO;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    public List<RideDTO> getRideHistoryByPassenger(Long passengerId) {
        List<Ride> rides = rideRepository.findByPassengerId(passengerId);
        return rides.stream().map(this::convertToRideDTO).collect(Collectors.toList());
    }

    public List<RideDTO> getRideHistoryByDriver(Long driverId) {
        List<Ride> rides = rideRepository.findByDriverId(driverId);
        return rides.stream().map(this::convertToRideDTO).collect(Collectors.toList());
    }

    public boolean cancelRide(Long rideId, String reason) {
        Optional<Ride> ride = rideRepository.findById(rideId);
        if (ride.isPresent() && ride.get().getRideStatus() != Ride.RideStatus.CANCELLED && ride.get().getRideStatus() != Ride.RideStatus.COMPLETED) {
            Ride r = ride.get();
            r.setRideStatus(Ride.RideStatus.CANCELLED);
            r.setCancellationReason(reason);
            rideRepository.save(r);
            return true;
        }
        return false;
    }

    private RideDTO convertToRideDTO(Ride ride) {
        RideDTO dto = new RideDTO();
        dto.setId(ride.getId());
        dto.setStartLocation(ride.getStartLocation());
        dto.setEndLocation(ride.getEndLocation());
        dto.setStartTime(ride.getStartTime());
        dto.setEndTime(ride.getEndTime());
        dto.setRideStatus(ride.getRideStatus().name());
        dto.setDriverId(ride.getDriver().getId());
        dto.setPassengerId(ride.getPassenger().getId());
        return dto;
    }

    private UserDTO convertToUserDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        return dto;
    }
}
