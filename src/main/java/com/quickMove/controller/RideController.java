package com.quickMove.controller;

import com.quickMove.Service.UserService;
import com.quickMove.model.Ride;
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
    public ResponseEntity<List<Ride>> getRideHistory(@RequestParam Long userId) {
        String role = userService.getUserRole(userId);
        List<Ride> history = null;

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
            @RequestParam String reason,
            @RequestParam Long userId) {

        boolean isCancelled = rideService.cancelRide(rideId, reason, userId);
        if (isCancelled) {
            return ResponseEntity.ok("Ride cancelled successfully");
        } else {
            return ResponseEntity.status(400).body("Ride cancellation failed or not authorized");
        }
    }
}
