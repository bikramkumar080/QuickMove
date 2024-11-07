package com.quickMove.controller;

import com.quickMove.Service.UserService;
import com.quickMove.dto.RideDTO;
import com.quickMove.Service.RideService;
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
    public ResponseEntity<String> cancelRide( //TODO check if the user has authorization to cancel the ride
            @RequestParam Long rideId,
            @RequestParam String reason) {

        boolean isCancelled = rideService.cancelRide(rideId, reason);
        if (isCancelled) {
            return ResponseEntity.ok("Ride cancelled successfully");
        } else {
            return ResponseEntity.status(400).body("Ride cancellation failed or not authorized");
        }
    }
}
