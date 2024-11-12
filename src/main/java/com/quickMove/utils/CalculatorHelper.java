package com.quickMove.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorHelper {

    private static final double EARTH_RADIUS_KM = 6371.0; // Radius of the Earth in kilometers

    // Calculate the distance between two points on the Earth's surface using the Haversine formula
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    public static int convertToMinutes(String duration) {
        int totalMinutes = 0;

        Pattern pattern = Pattern.compile("(\\d+)\\s*day[s]?|\\d+\\s*hour[s]?|\\d+\\s*minute[s]?");
        Matcher matcher = pattern.matcher(duration);

        while (matcher.find()) {
            String match = matcher.group();
            if (match.contains("day")) {
                int days = Integer.parseInt(match.split("\\s+")[0]);
                totalMinutes += days * 24 * 60;
            } else if (match.contains("hour")) {
                int hours = Integer.parseInt(match.split("\\s+")[0]);
                totalMinutes += hours * 60;
            } else if (match.contains("minute")) {
                int minutes = Integer.parseInt(match.split("\\s+")[0]);
                totalMinutes += minutes;
            }
        }

        return totalMinutes;
    }
}
