package com.quickMove.controller;

import com.quickMove.model.Ride;
import com.quickMove.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/rides")
public class BookingController {

    @Autowired
    private BookingService bookingService;

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
