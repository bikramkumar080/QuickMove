package com.quickMove.controller;

import com.quickMove.model.User;
import com.quickMove.repository.UserRepository;
import com.quickMove.service.UserAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("/api/users")
@RestController
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private UserRepository userRepository;
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody User request) {
        try {
            User user = userAuthService.register(request);
            return ResponseEntity.created(URI.create("/users/" + user.getId())).body(user);
        } catch (UserAuthService.ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("A user with this email already exists");
        }catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to perform this action please contact developer@quickmove.com");
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User request){
        try {
            String token = userAuthService.login(request);
            User user=userRepository.findByName(request.getName());
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Authentication successful");
            response.put("data", Map.of(
                    "access_token", token,
                    "token_type", "Bearer",
                    "expires_in", 3600, // Token expiration time in seconds (1 hour)
                    "user", Map.of(
                            "id", user.getId(),
                            "name", user.getName(),
                            "email", user.getEmail(),
                            "role", user.getRole()
                    )
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(Map.of(
                    "status", "error",
                    "message", "Invalid Credentials"
            ));
        }
    }

}

