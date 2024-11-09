package com.quickMove.service;

import com.quickMove.model.Ride;
import com.quickMove.model.User;
import com.quickMove.repository.RideRepository;
import com.quickMove.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.quickMove.dto.RideDTO;
import com.quickMove.dto.UserDTO;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    public List<RideDTO> getRideHistoryByPassenger(Long passengerId) {
        List<Ride> rides = rideRepository.findByPassengerIdAndStatusIn(
                passengerId,
                Arrays.asList("COMPLETED", "CANCELLED")
        );
        return rides.stream()
                    .map(this::convertToRideDTO)
                    .collect(Collectors.toList());
    }

    public List<RideDTO> getRideHistoryByDriver(Long driverId) {
        List<Ride> rides = rideRepository.findByDriverIdAndStatusIn(
                driverId,
                Arrays.asList("COMPLETED", "CANCELLED")
        );

        // Convert to DTO
        return rides.stream()
                    .map(this::convertToRideDTO)
                    .collect(Collectors.toList());
    }

    public boolean cancelRide(Long rideId, String reason) {
        Optional<Ride> ride = rideRepository.findById(rideId);
        if (ride.isPresent() && ride.get().getStatus() != Ride.Status.CANCELLED && ride.get().getStatus() != Ride.Status.COMPLETED) {
            Ride r = ride.get();
            r.setStatus(Ride.Status.CANCELLED);
            r.setCancellationReason(reason);
            rideRepository.save(r);
            return true;
        }
        return false;
    }

    private RideDTO convertToRideDTO(Ride ride) {
        RideDTO rideDTO = new RideDTO();
        rideDTO.setId(ride.getId());
        rideDTO.setStartLocation(ride.getStartLocation());
        rideDTO.setEndLocation(ride.getEndLocation());
        rideDTO.setStartTime(ride.getStartTime());
        rideDTO.setEndTime(ride.getEndTime());
        rideDTO.setStatus(ride.getStatus().name());
        rideDTO.setPrice(rideDTO.getPrice());
        if (ride.getDriver() != null) {
            rideDTO.setDriverId(ride.getDriver().getId());
        }
        if (ride.getPassenger() != null) {
            rideDTO.setPassengerId(ride.getPassenger().getId());
        }
        return rideDTO;
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

    public List<RideDTO> checkRideRequest() {
        List<Ride> rides = rideRepository.findByStatus(Ride.Status.UNASSIGNED);
        return rides.stream().map(this::convertToRideDTO).collect(Collectors.toList());
    }

    public RideDTO acceptRideRequest(String header,Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                                  .orElseThrow(() -> new RuntimeException("Ride not found"));
        if(ride.getDriver()==null || ride.getStatus()!= Ride.Status.ASSIGNED) {
            String userName = jwtService.extractUserName(header.substring(7));
            User user = userRepository.findByName(userName);
            User driver = userRepository.findById(user.getId())
                                        .orElseThrow(() -> new RuntimeException("Passenger not found"));
            ride.setDriver(driver);
            ride.setStatus(Ride.Status.ASSIGNED);
            ride.setStartTime(LocalDateTime.now());
            ride = rideRepository.save(ride);
            return convertToRideDTO(ride);
        }else{
            throw new RuntimeException("Already Accepted");
        }
    }

    public RideDTO completeRide(String header,Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                                  .orElseThrow(() -> new RuntimeException("Ride not found"));
        String userName = jwtService.extractUserName(header.substring(7));
        User user=userRepository.findByName(userName);
        if(Objects.equals(user.getRole(), "driver")) {
            if (ride.getStatus()!= Ride.Status.COMPLETED) {
                ride.setEndTime(LocalDateTime.now());
                ride.setStatus(Ride.Status.COMPLETED);
                ride = rideRepository.save(ride);
                return convertToRideDTO(ride);
            }
            else {
                throw new RuntimeException("Already Completed");
            }
        }
        else {throw new RuntimeException("You are not authorized to perform this action"); }
    }

    public List<RideDTO> listFilteredRides(Ride.Status status, Long driverId, Long passengerId) {
        List<Ride> rides = rideRepository.findAll().stream()
                .filter(ride -> (status == null || ride.getStatus() == status))
                .filter(ride -> (driverId == null || (ride.getDriver() != null && ride.getDriver().getId().equals(driverId))))
                .filter(ride -> (passengerId == null || ride.getPassenger().getId().equals(passengerId)))
                .collect(Collectors.toList());

        return rides.stream().map(this::convertToRideDTO).collect(Collectors.toList());
    }

    public RideDTO saveRide(Ride ride) {
        Ride savedRide = rideRepository.save(ride);
        return convertToRideDTO(savedRide);
    }

    public Optional<RideDTO> findRideById(Long id) {
        return rideRepository.findById(id).map(this::convertToRideDTO);
    }

    public Optional<RideDTO> updateRide(Long id, Ride rideDetails) {
        return rideRepository.findById(id).map(ride -> {
            ride.setStatus(rideDetails.getStatus());
            ride.setDriver(rideDetails.getDriver());
            ride.setPassenger(rideDetails.getPassenger());
            ride.setStartLocation(rideDetails.getStartLocation());
            ride.setEndLocation(rideDetails.getEndLocation());
            ride.setStartTime(rideDetails.getStartTime());
            ride.setEndTime(rideDetails.getEndTime());
            ride.setCancellationReason(rideDetails.getCancellationReason());
            Ride updatedRide = rideRepository.save(ride);
            return convertToRideDTO(updatedRide);
        });
    }

    public boolean deleteRide(Long id) {
        if (rideRepository.existsById(id)) {
            rideRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
