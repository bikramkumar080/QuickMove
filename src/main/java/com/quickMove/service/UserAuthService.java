package com.quickMove.service;

import com.quickMove.model.User;
import com.quickMove.repository.UserRepository;
import com.quickMove.service.Impl.JWTServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTServiceImpl jwtService;

    private final PasswordEncoder passwordEncoder;

    public UserAuthService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    public class ConflictException extends RuntimeException {
        public ConflictException(String message) {
            super(message);
        }
    }

    public User register(User request){
        User user = new User();
        if (repository.existsByEmail(request.getEmail())) {
            throw new ConflictException("A user with this email already exists.");
        }
        if (repository.existsByPhone(request.getPhone())) {
            throw new ConflictException("A user with this phone number already exists.");
        }
        if(request.getRole().equals("admin")){
            throw new AccessDeniedException("You are not authorized to perform this action");
        }
        user.setId(request.getId());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setPhone(request.getPhone());
        return repository.save(user);
    }

    public String login(User request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getName(), request.getPassword()));
            User user = repository.findByName(request.getName());
            if (user != null) {
                return jwtService.generateToken(user, request.getRole());
            } else {
                throw new IllegalArgumentException("User does not exist");
            }
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid credentials", e);
        }
    }



}
