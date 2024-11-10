package com.quickMove.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeoLocationFetcher {

//    @Value("${google.maps.api.key}")
    public static String API_KEY="##";
    public static double[] getCoordinates(String place) {
        try {
            if(API_KEY.equals("##")){
                return new double[]{10.10,20.20};
            }
            place = place.replace(" ", "+");
            String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=" + place + "&key=" + API_KEY;
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.toString());
            JsonNode results = jsonNode.get("results");

            if (results.size() > 0) {
                JsonNode location = results.get(0).get("geometry").get("location");
                double lat = location.get("lat").asDouble();
                double lng = location.get("lng").asDouble();

                return new double[] { lat, lng };
            } else {
                System.out.println("No results found.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}

