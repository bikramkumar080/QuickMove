package com.quickMove.controller;

import com.quickMove.model.User;
import com.quickMove.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequestMapping("/api/users")
@RestController
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody User request) {
        try {
            User user = userAuthService.register(request);
            return ResponseEntity.created(URI.create("/users/" + user.getId())).body(user);
        } catch (UserAuthService.ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("A user with this email already exists");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User request){
        try {
            return ResponseEntity.ok(userAuthService.login(request));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Invalid Credentials");
        }

    }

}

