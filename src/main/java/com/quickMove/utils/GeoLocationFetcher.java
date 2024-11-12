package com.quickMove.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GeoLocationFetcher {

//    @Value("${google.maps.api.key}")
    public static String API_KEY="##";
    private static final String DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";

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

    public static Map<String, String> calculateRouteMetrix(String pickupLocation, String dropLocation){
        String url = String.format("%s?origins=%s&destinations=%s&key=%s",
                DISTANCE_MATRIX_URL,
                pickupLocation,
                dropLocation,
                API_KEY);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String responseBody = "{\n" +
                "   \"destination_addresses\" : [\n" +
                "      \"Los Angeles, CA, USA\"\n" +
                "   ],\n" +
                "   \"origin_addresses\" : [\n" +
                "      \"New York, NY, USA\"\n" +
                "   ],\n" +
                "   \"rows\" : [\n" +
                "      {\n" +
                "         \"elements\" : [\n" +
                "            {\n" +
                "               \"distance\" : {\n" +
                "                  \"text\" : \"2,451 mi\",\n" +
                "                  \"value\" : 3945032\n" +
                "               },\n" +
                "               \"duration\" : {\n" +
                "                  \"text\" : \"1 day 14 hours\",\n" +
                "                  \"value\" : 137440\n" +
                "               },\n" +
                "               \"status\" : \"OK\"\n" +
                "            }\n" +
                "         ]\n" +
                "      }\n" +
                "   ],\n" +
                "   \"status\" : \"OK\"\n" +
                "}";

//        Map<String, String> responseOutcome=parseDistanceMatrixResponse(response.getBody());

        return parseDistanceMatrixResponse(responseBody);
    }

    private static Map<String, String> parseDistanceMatrixResponse(String responseBody) {
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
}

