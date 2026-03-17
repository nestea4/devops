package com.example.busservice.repository;/*
    @author User
    @project lab4
    @class TripRepository
    @version 1.0.0
    @since 28.04.2025 - 17.42 
*/

import com.example.busservice.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByRouteId(Long routeId);
    List<Trip> findByBusId(Long busId);
    List<Trip> findByDepartureTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Trip> findByRouteIdAndDepartureTimeBetween(Long routeId, LocalDateTime start, LocalDateTime end);
    List<Trip> findByAvailableSeatsGreaterThanEqual(Integer seats);
}
