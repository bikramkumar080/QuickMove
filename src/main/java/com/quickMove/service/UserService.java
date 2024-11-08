package com.quickMove.service;

import com.quickMove.model.User;
import com.quickMove.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public String getUserRole(Long userId) {
        User user = userRepository.findUserById(userId);
        if (user != null) {
            return user.getRole(); // Assuming User has a getRole() method
        }
        return null;
    }
}