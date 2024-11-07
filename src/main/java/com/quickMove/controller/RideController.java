package com.quickMove.controller;

import com.quickMove.model.Ride;
import com.quickMove.service.UserService;
import com.quickMove.dto.RideDTO;
import com.quickMove.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ride")
public class RideController {

    @Autowired
    private RideService rideService;

    @Autowired
    private UserService userService;

    @GetMapping("/history")
    public ResponseEntity<List<RideDTO>> getRideHistory(@RequestParam Long userId) {  //TODO check if userID has authorization to view the history
        String role = userService.getUserRole(userId);
        List<RideDTO> history = null;

        if ("PASSENGER".equalsIgnoreCase(role)) {
            history = rideService.getRideHistoryByPassenger(userId);
        } else if ("DRIVER".equalsIgnoreCase(role)) {
            history = rideService.getRideHistoryByDriver(userId);
        }

        if (history == null || history.isEmpty()) {
            return ResponseEntity.status(404).body(null); // No rides found for the given userId
        }
        return ResponseEntity.ok(history);
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelRide(
            @RequestParam Long rideId,
            @RequestParam String reason) {

        boolean isCancelled = rideService.cancelRide(rideId, reason);
        if (isCancelled) {
            return ResponseEntity.ok("Ride cancelled successfully");
        } else {
            return ResponseEntity.status(400).body("Ride cancellation failed or not authorized");
        }
    }
    @GetMapping("/check/request")
    public ResponseEntity<List<RideDTO>> getAllRides() {
        List<RideDTO> rides = rideService.checkRideRequest();
        return ResponseEntity.ok(rides);
    }

    @PostMapping("/accept")
    public ResponseEntity<RideDTO> acceptRequest(@RequestHeader("Authorization") String header,
            @RequestParam Long requestId) {
        RideDTO updatedRide = rideService.acceptRideRequest(header,requestId);
        return ResponseEntity.ok(updatedRide);
    }

    @PostMapping("/complete")
    public ResponseEntity<RideDTO> completeRide(@RequestHeader("Authorization") String header,
            @RequestParam Long rideId) {
        RideDTO updatedRide = rideService.completeRide(header,rideId);
        return ResponseEntity.ok(updatedRide);
    }


}
