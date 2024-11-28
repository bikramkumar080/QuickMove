package com.quickMove.controller;

import com.quickMove.model.User;
import com.quickMove.model.VehicleType;
import com.quickMove.service.UserService;
import com.quickMove.dto.RideDTO;
import com.quickMove.service.RideService;
import com.quickMove.service.VehicleTypeService;
import com.quickMove.utils.CalculatorHelper;
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
    @Autowired
    private VehicleTypeService vehicleTypeService;

    @GetMapping("/history")
    public ResponseEntity<List<RideDTO>> getRideHistory(@RequestParam Long userId, @RequestParam int page, @RequestParam(required = false, defaultValue = "10") int count) {
        try {
            String role = userService.getUserRole(userId);
            List<RideDTO> history = null;
            if ("PASSENGER".equalsIgnoreCase(role)) {
                history = rideService.getRideHistoryByPassenger(userId, page, count);
            } else if ("DRIVER".equalsIgnoreCase(role)) {
                history = rideService.getRideHistoryByDriver(userId, page, count);
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
    public ResponseEntity<Object> getAllRides(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            User user = userService.fetchUserDetails(authorizationHeader);
            VehicleType vehicleType = vehicleTypeService.fetchVehiclesTypesById(user.getVehicleType().getId());
            List<RideDTO> rides = rideService.searchRides(vehicleType.getId());
            if (rides.isEmpty()) {
                return ResponseEntity.ok().body(new HashMap<String, Object>() {{
                    put("message", "No requests available");
                    put("data", new ArrayList<>());
                }});
            }
            return ResponseEntity.ok().body(new HashMap<String, Object>() {{
                put("message", "Rides found successfully");
                put("data", rides);
            }});
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new HashMap<String, Object>() {{
                                     put("message", "An error occurred while fetching the rides.");
                                 }});
        }
    }


    @PostMapping("/accept")
    public ResponseEntity<Map<String, Object>> acceptRequest(@RequestHeader("Authorization") String header,
            @RequestParam Long rideId) {
        try {
            RideDTO updatedRide = rideService.acceptRideRequest(header, rideId);
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
            @RequestParam Long rideId, @RequestParam double latitude, @RequestParam double longitude) {
        try {
            RideDTO updatedRide = rideService.completeRide(header,rideId);
            double distanceRemaining = CalculatorHelper.calculateDistance(updatedRide.getEndLocationLatitude(), updatedRide.getEndLocationLongitude(), latitude, longitude);
            if(distanceRemaining*1000 > 100){
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Forceful Ride Completion");
                errorResponse.put("remainingDistance (km)", distanceRemaining);
                errorResponse.put("rideDetails", updatedRide);
                return ResponseEntity.status(455).body(errorResponse);
            }
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

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startRide(@RequestParam Long rideId) {
        try {
            RideDTO updatedRide = rideService.startRide(rideId);
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "Ride Started");
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
