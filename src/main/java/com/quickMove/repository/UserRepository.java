package com.quickMove.repository;

import com.quickMove.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserById(Long id);
    Optional<User> findByEmail(String email);
    User findByPhone(String phone);
    User findByName(String name);
}
