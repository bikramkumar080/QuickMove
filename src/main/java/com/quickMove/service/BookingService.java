package com.quickMove.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickMove.model.Ride;
import com.quickMove.model.User;
import com.quickMove.repository.RideRepository;
import com.quickMove.repository.UserRepository;
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

    @Value("${google.maps.api.key}")
    private String apiKey;

    private final String DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";

    public Ride getDistanceAndTime(String pickupLocation, String dropLocation,String header) {

        // Build the Google Maps API URL
        String url = String.format("%s?origins=%s&destinations=%s&key=%s",
                DISTANCE_MATRIX_URL,
                pickupLocation,
                dropLocation,
                apiKey);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//        String responseBody = "{\n" +
//                "   \"destination_addresses\" : [\n" +
//                "      \"Los Angeles, CA, USA\"\n" +
//                "   ],\n" +
//                "   \"origin_addresses\" : [\n" +
//                "      \"New York, NY, USA\"\n" +
//                "   ],\n" +
//                "   \"rows\" : [\n" +
//                "      {\n" +
//                "         \"elements\" : [\n" +
//                "            {\n" +
//                "               \"distance\" : {\n" +
//                "                  \"text\" : \"2,451 mi\",\n" +
//                "                  \"value\" : 3945032\n" +
//                "               },\n" +
//                "               \"duration\" : {\n" +
//                "                  \"text\" : \"1 day 14 hours\",\n" +
//                "                  \"value\" : 137440\n" +
//                "               },\n" +
//                "               \"status\" : \"OK\"\n" +
//                "            }\n" +
//                "         ]\n" +
//                "      }\n" +
//                "   ],\n" +
//                "   \"status\" : \"OK\"\n" +
//                "}";

        Map<String, String> responseOutcome=parseDistanceMatrixResponse(response.getBody());

//        Map<String, String> responseOutcome= parseDistanceMatrixResponse(responseBody);
        Ride ride =new Ride();
        if (responseOutcome.get("distance")!=null && responseOutcome.get("duration")!=null){

            //Price should be calculate how?

            String userName = jwtService.extractUserName(header.substring(7));
            User user=userRepository.findByName(userName);
            User passenger = userRepository.findById(user.getId())
                                           .orElseThrow(() -> new RuntimeException("Passenger not found"));
            ride.setDriver(null);
            ride.setPassenger(passenger);
            ride.setStartLocation(responseOutcome.get("pickup"));
            ride.setEndLocation(responseOutcome.get("drop"));
            ride.setStartTime(null);
            ride.setEndTime(null);
            ride.setPrice(100);
            ride.setRideStatus(Ride.RideStatus.PENDING);
            ride.setCancellationReason(null);

            return rideRepository.save(ride);
        }
        throw new RuntimeException("Distance or duration not found in the response.");
    }

    private Map<String, String> parseDistanceMatrixResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            String pickup = rootNode.path("origin_addresses").get(0).asText();
            String drop = rootNode.path("destination_addresses").get(0).asText();
            JsonNode elementNode = rootNode.path("rows").get(0).path("elements").get(0);
            String distance = elementNode.path("distance").path("text").asText();
            String duration = elementNode.path("duration").path("text").asText();

            // Return the result as a Map
            Map<String, String> result = new HashMap<>();
            result.put("distance", distance);
            result.put("duration", duration);
            result.put("pickup",pickup);
            result.put("drop",drop);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String modifyLocation(String location) {
        // Add the string " location" to the provided location name
        return location + " location";
    }

}
