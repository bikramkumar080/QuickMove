package com.quickMove.service;

import com.quickMove.model.User;
import com.quickMove.repository.UserRepository;
import com.quickMove.service.Impl.JWTServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    public User register(User request){
        User user = new User();
        user.setId(request.getId());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setPhone(request.getPhone());
        return repository.save(user);
    }

    public String login(User request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getName(),
                request.getPassword()));
        User user=repository.findByName(request.getName());
        if(user!=null){
            return jwtService.generateToken(user,request.getRole());

        }else {
            throw new IllegalArgumentException("Credentials are incorrect");
        }


    }

}
