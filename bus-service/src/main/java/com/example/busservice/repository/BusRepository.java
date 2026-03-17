package com.example.busservice.repository;/*
    @author User
    @project lab4
    @class BusRepository
    @version 1.0.0
    @since 28.04.2025 - 15.47 
*/

import com.example.busservice.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    List<Bus> findByType(Bus.BusType type);
    Bus findByNumber(String number);
}
