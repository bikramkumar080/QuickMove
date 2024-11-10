package com.quickMove.controller;

import com.quickMove.dto.VehicleDTO;
import com.quickMove.model.User;
import com.quickMove.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/driver")
public class DriverController {
    @Autowired
    private UserService userService;

    @PutMapping("/profile/{id}")
    public ResponseEntity updateProfile(@PathVariable Long id, @RequestBody VehicleDTO body) {
        User user = userService.updateDriverProfile(id, body);
        if(user != null) {
            return ResponseEntity.ok("Successfully updated profile");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
