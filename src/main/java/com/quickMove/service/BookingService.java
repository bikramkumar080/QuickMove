package com.quickMove.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickMove.dto.RideDTO;
import com.quickMove.model.Ride;
import com.quickMove.model.User;
import com.quickMove.repository.RideRepository;
import com.quickMove.repository.UserRepository;
import com.quickMove.utils.GeoLocationFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class BookingService {



    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private RideService rideService;

    public RideDTO createRideRequest(User passenger, double []pickupCoordinate, double []dropCoordinate) {
        Ride ride= new Ride();
        ride.setPassenger(passenger);
        ride.setStartLocationLatitude(pickupCoordinate[0]);
        ride.setStartLocationLongitude(pickupCoordinate[1]);
        ride.setEndLocationLatitude(dropCoordinate[0]);
        ride.setEndLocationLongitude(dropCoordinate[1]);
        ride.setStatus(Ride.Status.OFFERED);

        return rideService.convertToRideDTO(rideRepository.save(ride));
    }

    public Ride bookRide(String pickupLocation, String dropLocation,String header) {
        Map<String, String> responseOutcome = GeoLocationFetcher.calculateRouteMetrix(pickupLocation, dropLocation);
        double []pickupCoordinate = GeoLocationFetcher.getCoordinates(responseOutcome.get("pickup"));
        double []dropCoordinate = GeoLocationFetcher.getCoordinates(responseOutcome.get("drop"));
        Ride ride =new Ride();
        if (responseOutcome.get("distance")!=null && responseOutcome.get("duration")!=null){

            //Price should be calculate how?

            String userName = jwtService.extractUserName(header.substring(7));
            User user=userRepository.findByName(userName);
            User passenger = userRepository.findById(user.getId())
                                           .orElseThrow(() -> new RuntimeException("Passenger not found"));
            ride.setDriver(null);
            ride.setPassenger(passenger);
            ride.setStartLocationLatitude(pickupCoordinate[0]);
            ride.setStartLocationLongitude(pickupCoordinate[1]);
            ride.setEndLocationLatitude(dropCoordinate[0]);
            ride.setEndLocationLongitude(dropCoordinate[1]);
            ride.setStartTime(null);
            ride.setEndTime(null);
            ride.setPrice("100");
            ride.setStatus(Ride.Status.UNASSIGNED);
            ride.setCancellationReason(null);

            return rideRepository.save(ride);
        }
        throw new RuntimeException("Distance or duration not found in the response.");
    }

    public String modifyLocation(String location) {
        // Add the string " location" to the provided location name
        return location + " location";
    }

}
