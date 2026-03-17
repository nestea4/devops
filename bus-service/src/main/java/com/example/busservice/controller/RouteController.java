package com.example.busservice.controller;/*
    @author User
    @project lab4
    @class RouteController
    @version 1.0.0
    @since 28.04.2025 - 17.53 
*/

import com.example.busservice.DTO.RouteDTO;
import com.example.busservice.DTO.RouteResponseDTO;
import com.example.busservice.mappers.RouteMapper;
import com.example.busservice.model.Route;
import com.example.busservice.service.RouteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;
    private final RouteMapper routeMapper;

    @Autowired
    public RouteController(RouteService routeService, RouteMapper routeMapper) {
        this.routeService = routeService;
        this.routeMapper = routeMapper;
    }

    @GetMapping
    public ResponseEntity<List<RouteResponseDTO>> getAllRoutes() {
        List<Route> routes = routeService.findAll();
        List<RouteResponseDTO> routeResponseDTOs = routeMapper.toRouteResponseList(routes);
        return ResponseEntity.ok(routeResponseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteResponseDTO> getRouteById(@PathVariable Long id) {
        Route route = routeService.findById(id);
        return ResponseEntity.ok(routeMapper.toRouteResponse(route));
    }

    @GetMapping("/departure/{city}")
    public ResponseEntity<List<RouteResponseDTO>> getRoutesByDepartureCity(@PathVariable String city) {
        List<Route> routes = routeService.findByDepartureCity(city);
        List<RouteResponseDTO> routeResponseDTOs = routeMapper.toRouteResponseList(routes);
        return ResponseEntity.ok(routeResponseDTOs);
    }

    @GetMapping("/arrival/{city}")
    public ResponseEntity<List<RouteResponseDTO>> getRoutesByArrivalCity(@PathVariable String city) {
        List<Route> routes = routeService.findByArrivalCity(city);
        List<RouteResponseDTO> routeResponseDTOs = routeMapper.toRouteResponseList(routes);
        return ResponseEntity.ok(routeResponseDTOs);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RouteResponseDTO>> searchRoutes(
            @RequestParam String departureCity,
            @RequestParam String arrivalCity) {
        List<Route> routes = routeService.findByDepartureCityAndArrivalCity(departureCity, arrivalCity);
        List<RouteResponseDTO> routeResponseDTOs = routeMapper.toRouteResponseList(routes);
        return ResponseEntity.ok(routeResponseDTOs);
    }

    @PostMapping
    public ResponseEntity<RouteResponseDTO> createRoute(@Valid @RequestBody RouteDTO routeDTO) {
        Route route = routeMapper.toRoute(routeDTO);
        Route savedRoute = routeService.save(route);
        RouteResponseDTO routeResponseDTO = routeMapper.toRouteResponse(savedRoute);
        return ResponseEntity.status(HttpStatus.CREATED).body(routeResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RouteResponseDTO> updateRoute(@PathVariable Long id, @Valid @RequestBody RouteDTO routeDTO) {
        Route route = routeMapper.toRoute(routeDTO);
        Route updatedRoute = routeService.update(id, route);
        RouteResponseDTO routeResponseDTO = routeMapper.toRouteResponse(updatedRoute);
        return ResponseEntity.ok(routeResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}