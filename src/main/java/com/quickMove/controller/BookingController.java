package com.quickMove.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickMove.dto.RideDTO;
import com.quickMove.dto.VehicleTypeCostDto;
import com.quickMove.model.Organization;
import com.quickMove.model.Ride;
import com.quickMove.model.User;
import com.quickMove.model.VehicleType;
import com.quickMove.service.BookingService;
import com.quickMove.service.Impl.OrganizationService;
import com.quickMove.service.RideService;
import com.quickMove.service.UserService;
import com.quickMove.service.VehicleTypeService;
import com.quickMove.utils.CalculatorHelper;
import com.quickMove.utils.GeoLocationFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rides")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RideService rideService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UserService userService;
    @Autowired
    private VehicleTypeService vehicleTypeService;

    @PostMapping("/search")
    public ResponseEntity<Object> searchRides(@RequestHeader("Authorization") String authorizationHeader,
                                                     @RequestParam String pickupLocation,
                                                     @RequestParam String dropLocation) {
        try {
            Map<String, String> routeMetrix = GeoLocationFetcher.calculateRouteMetrix(pickupLocation, dropLocation);
            double[] pickupCoordinate = GeoLocationFetcher.getCoordinates(routeMetrix.get("pickup"));
            double[] dropCoordinate = GeoLocationFetcher.getCoordinates(routeMetrix.get("drop"));
            if (routeMetrix.get("distance") != null && routeMetrix.get("duration") != null) {
                User user = userService.fetchUserDetails(authorizationHeader);
                Organization organization = organizationService.fetchOrganizationById(user.getOrganization().getId());
                List<VehicleType> vehicleTypeList = vehicleTypeService.fetchVehiclesTypesOfOrg(organization.getId());
                List<VehicleTypeCostDto> responseMapData = new ArrayList<>();
                for(VehicleType vehicleTypeObject: vehicleTypeList) {
                    String costCalculationUrl = organization.getCostCalculationUrl();
                    String vehicleType = vehicleTypeObject.getType();
                    RestTemplate restTemplate = new RestTemplate();
                    costCalculationUrl += "?distance=" + routeMetrix.get("distance").split(" ")[0].replace(",","") +
                            "&duration=" + CalculatorHelper.convertToMinutes(routeMetrix.get("duration")) +
                            "&ride_type=" + vehicleType +
                            "&pickup_lat=" + pickupCoordinate[0] + "&pickup_lon=" + pickupCoordinate[1] +
                            "&drop_lat=" + dropCoordinate[0] + "&drop_lon=" + dropCoordinate[1];
                    ResponseEntity<String> response = restTemplate.getForEntity(costCalculationUrl, String.class);
                    if (response.getStatusCode().is2xxSuccessful()) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            JsonNode jsonNode = objectMapper.readTree(response.getBody());
                            responseMapData.add(new VehicleTypeCostDto(vehicleTypeObject, jsonNode.get("cost").asText()));
                        } catch (JsonProcessingException e) {
                            return ResponseEntity.status(466).body(new HashMap<String, Object>() {{
                                put("message", "Invalid structured response from cost calculation api");
                            }});
                        }
                    } else {
                        ResponseEntity.status(555).body(new HashMap<String, Object>() {{
                            put("message", "Error during cost calculation");
                        }});
                    }
                }
                RideDTO ride = bookingService.createRideRequest(user, pickupCoordinate, dropCoordinate);
                return ResponseEntity.ok().body(new HashMap<String, Object>() {{
                    put("message", "Rides found successfully");
                    put("data", responseMapData);
                    put("ride", ride);
                }});
            } else {
                ResponseEntity.status(444).body(new HashMap<String, Object>() {{
                    put("message", "Error during route calculation:  Unidentifiable locations");
                }});
            }
        } catch(Exception e) {
            return ResponseEntity.status(500).body(new HashMap<String, Object>() {{
                put("message", "An error occurred while searching for rides. Please try again.");
            }});
        }
        return null;
    }

    @PostMapping("/book")
    public ResponseEntity<Object> getModifiedLocations(@RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String pickupLocation,
            @RequestParam String dropLocation) {
        try {
            Ride bookedRide = bookingService.bookRide(pickupLocation, dropLocation, authorizationHeader);
            if (bookedRide != null) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                     .body(new HashMap<String, Object>() {{
                                         put("message", "Ride successfully booked");
                                         put("ride", bookedRide);
                                     }});
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body(new HashMap<String, Object>() {{
                                         put("message", "Unable to book ride. Please try again.");
                                     }});
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new HashMap<String, Object>() {{
                                     put("message", "An error occurred while booking the ride. Please try again.");
                                 }});
        }
    }

}
