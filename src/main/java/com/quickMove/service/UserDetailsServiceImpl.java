package com.quickMove.service;

import com.quickMove.model.User;
import com.quickMove.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByName(username);
        if(user != null){
            return org.springframework.security.core.userdetails.User.builder()
                                                                     .username(user.getName())
                                                                     .password(user.getPassword())
                                                                     .roles(user.getRole()).build();
        }else{

        }
        throw new UsernameNotFoundException("User not found with username");
    }
}
