package com.quickMove.service;

import org.springframework.stereotype.Service;

@Service
public class FareCalculationService {

    private static final double BASE_FARE = 3.00;
    private static final double PER_KM_RATE = 1.50;
    private static final double PER_MINUTE_RATE = 0.50;
    private static final double MINIMUM_FARE = 5.00;
    private static final double SURGE_FACTOR = 1.2;
    private static final double ADDITIONAL_FEES = 2.00;  // e.g., tolls or other charges

    public double calculateFare(double distance, double time, boolean isSurgePricing) {
        // Calculate the base fare components
        double distanceFare = distance * PER_KM_RATE;
        double timeFare = time * PER_MINUTE_RATE;
        
        // Total fare without surge pricing
        double totalFare = BASE_FARE + distanceFare + timeFare + ADDITIONAL_FEES;

        // Apply surge pricing if needed
        if (isSurgePricing) {
            totalFare *= SURGE_FACTOR;
        }

        // Ensure the fare meets the minimum fare requirement
        totalFare = Math.max(totalFare, MINIMUM_FARE);

        return totalFare;
    }
}

