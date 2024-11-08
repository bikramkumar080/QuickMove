package com.quickMove.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UserServices {
    UserDetailsService userDetailsService();
}
