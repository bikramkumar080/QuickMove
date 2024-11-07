package com.quickMove.controller;

import com.quickMove.model.User;
import com.quickMove.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User request){
        return ResponseEntity.ok(userAuthService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User request){
        return ResponseEntity.ok(userAuthService.login(request));
    }
    @GetMapping("api/getData")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hi Passenger");
    }



}

