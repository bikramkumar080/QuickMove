package com.quickMove.controller;

import com.quickMove.dto.RideDTO;
import com.quickMove.dto.UserDTO;
import com.quickMove.model.Organization;
import com.quickMove.model.Ride;
import com.quickMove.model.User;
import com.quickMove.service.Impl.OrganizationService;
import com.quickMove.service.JWTService;
import com.quickMove.service.RideService;
import com.quickMove.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private RideService rideService;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private OrganizationService organizationService;

    @GetMapping("/rides")
    public ResponseEntity<?> getRides(
            @RequestParam(required = false) Ride.Status status,
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) Long passengerId) {

        List<RideDTO> rides = rideService.listFilteredRides(status, driverId, passengerId);
        return ResponseEntity.ok(rides);
    }

    @PostMapping("/ride")
    public ResponseEntity<?> createRide(@RequestBody Ride ride) {
        RideDTO savedRide = rideService.saveRide(ride);
        return ResponseEntity.ok(savedRide);
    }

    @GetMapping("/ride/{id}")
    public ResponseEntity<?> getRideById(@PathVariable Long id) {
        Optional<RideDTO> ride = rideService.findRideById(id);
        return ride.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/ride/{id}")
    public ResponseEntity<?> updateRide(@PathVariable Long id, @RequestBody Ride rideDetails) {
        Optional<RideDTO> updatedRide = rideService.updateRide(id, rideDetails);
        return updatedRide.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/ride/{id}")
    public ResponseEntity<?> deleteRide(@PathVariable Long id) {
        boolean isDeleted = rideService.deleteRide(id);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        List<UserDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/user")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        UserDTO savedUser = userService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<UserDTO> user = userService.findUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<UserDTO> updatedUser = userService.updateUser(id, userDetails);
        return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        boolean isDeleted = userService.deleteUser(id);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/cost-calc-endpoint")
    public ResponseEntity<String> registerCostCalculationEndpoint(@RequestHeader("Authorization") String header,
                                                                  @RequestParam  String costCalculationUrl,
                                                                  @RequestParam(required = false) String authToken) {
        User user = userService.fetchUserDetails(header);
        if(user!=null){
            Long organizationId = user.getOrganization().getId();
            Organization organization = organizationService.fetchOrganizationById(organizationId);
            if(organization != null) {
                organization.setCostCalculationUrl(costCalculationUrl);
                if(authToken != null) {
                    organization.setAuthToken(authToken);
                }
                organizationService.save(organization);
                return ResponseEntity.ok("Cost calculation endpoint registered successfully.");
            }
            return ResponseEntity.badRequest().body("Organization not found for the admin user.");
        }
        return ResponseEntity.badRequest().body("User not found.");
    }
}