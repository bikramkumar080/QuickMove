package com.quickMove.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.quickMove.service.FareCalculationService;

@RestController
@RequestMapping("/api/fare")
public class FareController {

    @Autowired
    private FareCalculationService fareCalculationService;

    @PostMapping("/calculate")
    public double calculateFare(@RequestParam double distance, 
                                @RequestParam double time, 
                                @RequestParam boolean isSurgePricing) {
        return fareCalculationService.calculateFare(distance, time, isSurgePricing);
    }
}
