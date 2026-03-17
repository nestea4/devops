package com.example.busservice.service;/*
    @author User
    @project lab4
    @class RouteService
    @version 1.0.0
    @since 28.04.2025 - 17.54 
*/

import com.example.busservice.exeption.InvalidStateException;
import com.example.busservice.exeption.ResourceNotFoundException;
import com.example.busservice.model.Route;
import com.example.busservice.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {

    private final RouteRepository routeRepository;

    @Autowired
    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public List<Route> findAll() {
        return routeRepository.findAll();
    }

    public Route findById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
    }

    public List<Route> findByDepartureCity(String departureCity) {
        List<Route> routes = routeRepository.findByDepartureCity(departureCity);
        if (routes.isEmpty()) {
            throw new ResourceNotFoundException("No routes found from departure city: " + departureCity);
        }
        return routes;
    }

    public List<Route> findByArrivalCity(String arrivalCity) {
        List<Route> routes = routeRepository.findByArrivalCity(arrivalCity);
        if (routes.isEmpty()) {
            throw new ResourceNotFoundException("No routes found to arrival city: " + arrivalCity);
        }
        return routes;
    }

    public List<Route> findByDepartureCityAndArrivalCity(String departureCity, String arrivalCity) {
        List<Route> routes = routeRepository.findByDepartureCityAndArrivalCity(departureCity, arrivalCity);
        if (routes.isEmpty()) {
            throw new ResourceNotFoundException("No routes found from " + departureCity + " to " + arrivalCity);
        }
        return routes;
    }

    public Route save(Route route) {
        validateRoute(route);
        return routeRepository.save(route);
    }

    public Route update(Long id, Route route) {
        if (!routeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Route not found with id: " + id);
        }

        validateRoute(route);
        route.setId(id);
        return routeRepository.save(route);
    }

    public void deleteById(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Route not found with id: " + id);
        }
        routeRepository.deleteById(id);
    }

    private void validateRoute(Route route) {
        if (route.getDepartureCity() == null || route.getDepartureCity().trim().isEmpty()) {
            throw new InvalidStateException("Departure city cannot be empty");
        }

        if (route.getArrivalCity() == null || route.getArrivalCity().trim().isEmpty()) {
            throw new InvalidStateException("Arrival city cannot be empty");
        }

        if (route.getDepartureCity().equals(route.getArrivalCity())) {
            throw new InvalidStateException("Departure and arrival cities cannot be the same");
        }
    }
}