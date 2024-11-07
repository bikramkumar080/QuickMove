package com.quickMove.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public interface JWTService {
    String extractUserName(String jwt);

    boolean isTokenValid(String jwt, UserDetails userDetails);
}
