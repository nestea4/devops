package com.example.userservice.repository;/*
    @author User
    @project lab4
    @class UserRepository
    @version 1.0.0
    @since 28.04.2025 - 15.19 
*/

import com.example.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
