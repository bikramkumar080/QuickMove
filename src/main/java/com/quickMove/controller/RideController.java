package com.quickMove.controller;

import com.quickMove.model.Ride;
import com.quickMove.service.UserService;
import com.quickMove.dto.RideDTO;
import com.quickMove.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/rides")
public class RideController {

    @Autowired
    private RideService rideService;

    @Autowired
    private UserService userService;

    @GetMapping("/history")
    public ResponseEntity<List<RideDTO>> getRideHistory(@RequestParam Long userId) {
        try {
            String role = userService.getUserRole(userId);
            List<RideDTO> history = null;
            if ("PASSENGER".equalsIgnoreCase(role)) {
                history = rideService.getRideHistoryByPassenger(userId);
            } else if ("DRIVER".equalsIgnoreCase(role)) {
                history = rideService.getRideHistoryByDriver(userId);
            }else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            if (history == null || history.isEmpty()) {
                return ResponseEntity.status(404).body(null);
            }
            return ResponseEntity.ok(history);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new ArrayList<>());
        }

    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelRide(
            @RequestParam Long rideId,
            @RequestParam String reason) {
        try {
            boolean isCancelled = rideService.cancelRide(rideId, reason);
            if (isCancelled) {
                return ResponseEntity.ok("Ride cancelled successfully");
            } else {
                return ResponseEntity.status(400).body("Ride cancellation failed or not authorized");
            }
        }catch (Exception e){
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }
    @GetMapping("/check")
    public ResponseEntity<Object> getAllRides() {
        try {
            List<RideDTO> rides = rideService.checkRideRequest();
            if (rides.isEmpty()) {
                return ResponseEntity.ok().body(new HashMap<String, Object>() {{
                    put("message", "No requests available");
                    put("data", new ArrayList<>());
                }});
            }
            return ResponseEntity.ok(rides);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new HashMap<String, Object>() {{
                                     put("message", "An error occurred while fetching the rides.");
                                 }});
        }
    }


    @PostMapping("/accept")
    public ResponseEntity<Map<String, Object>> acceptRequest(@RequestHeader("Authorization") String header,
            @RequestParam Long requestId) {
        try {
            RideDTO updatedRide = rideService.acceptRideRequest(header,requestId);
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "Ride accepted");
            successResponse.put("ride", updatedRide);
            return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
        }catch (RuntimeException e){
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        catch (Exception e){
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Sorry, current request not available now");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

    }

    @PostMapping("/complete")
    public ResponseEntity<Map<String, Object>> completeRide(@RequestHeader("Authorization") String header,
            @RequestParam Long rideId) {
        try {
            RideDTO updatedRide = rideService.completeRide(header,rideId);
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "Ride Completed");
            successResponse.put("ride", updatedRide);
            return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
        }catch (RuntimeException e){
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        }catch (Exception e){
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

    }


}
