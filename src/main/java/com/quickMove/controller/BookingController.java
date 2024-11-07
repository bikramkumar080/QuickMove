package com.quickMove.controller;

import com.quickMove.model.Ride;
import com.quickMove.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/book/ride")
    public Ride getModifiedLocations(@RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String pickupLocation,
            @RequestParam String dropLocation) {
        String modifiedPickupLocation = bookingService.modifyLocation(pickupLocation);
        String modifiedDropLocation = bookingService.modifyLocation(dropLocation);
        return bookingService.getDistanceAndTime(pickupLocation,dropLocation,authorizationHeader);
    }
}
