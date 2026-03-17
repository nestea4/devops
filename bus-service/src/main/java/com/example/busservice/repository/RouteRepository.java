package com.example.busservice.repository;/*
    @author User
    @project lab4
    @class RouteRepository
    @version 1.0.0
    @since 28.04.2025 - 17.55 
*/

import com.example.busservice.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByDepartureCity(String departureCity);
    List<Route> findByArrivalCity(String arrivalCity);
    List<Route> findByDepartureCityAndArrivalCity(String departureCity, String arrivalCity);
}

