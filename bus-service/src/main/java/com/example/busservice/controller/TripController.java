package com.example.busservice.controller;/*
    @author User
    @project lab4
    @class TripController
    @version 1.0.0
    @since 28.04.2025 - 17.38 
*/

import com.example.busservice.DTO.BusDTO;
import com.example.busservice.DTO.RouteDTO;
import com.example.busservice.DTO.TripDTO;
import com.example.busservice.DTO.TripResponseDTO;
import com.example.busservice.exeption.ResourceNotFoundException;
import com.example.busservice.mappers.TripMapper;
import com.example.busservice.model.Bus;
import com.example.busservice.model.Route;
import com.example.busservice.model.Trip;
import com.example.busservice.service.TripService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;
    private final TripMapper tripMapper;

    @Autowired
    public TripController(TripService tripService, TripMapper tripMapper) {
        this.tripService = tripService;
        this.tripMapper = tripMapper;
    }

    @GetMapping
    public ResponseEntity<List<TripResponseDTO>> getAllTrips() {
        List<Trip> trips = tripService.findAll();
        List<TripResponseDTO> tripResponseDTOs = tripMapper.toTripResponseList(trips);
        return ResponseEntity.ok(tripResponseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponseDTO> getTripById(@PathVariable Long id) {
        Trip trip = tripService.findById(id);
        TripResponseDTO tripResponseDTO = tripService.enrichTripResponse(tripMapper.toTripResponse(trip));
        return ResponseEntity.ok(tripResponseDTO);
    }

    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<TripResponseDTO>> getTripsByRouteId(@PathVariable Long routeId) {
        List<Trip> trips = tripService.findByRouteId(routeId);
        List<TripResponseDTO> tripResponseDTOs = tripMapper.toTripResponseList(trips);
        tripResponseDTOs = tripService.enrichTripResponseList(tripResponseDTOs);
        return ResponseEntity.ok(tripResponseDTOs);
    }

    @GetMapping("/bus/{busId}")
    public ResponseEntity<List<TripResponseDTO>> getTripsByBusId(@PathVariable Long busId) {
        List<Trip> trips = tripService.findByBusId(busId);
        List<TripResponseDTO> tripResponseDTOs = tripMapper.toTripResponseList(trips);
        tripResponseDTOs = tripService.enrichTripResponseList(tripResponseDTOs);
        return ResponseEntity.ok(tripResponseDTOs);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<TripResponseDTO>> getTripsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Trip> trips = tripService.findByDepartureTimeBetween(start, end);
        List<TripResponseDTO> tripResponseDTOs = tripMapper.toTripResponseList(trips);
        tripResponseDTOs = tripService.enrichTripResponseList(tripResponseDTOs);
        return ResponseEntity.ok(tripResponseDTOs);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TripResponseDTO>> searchTrips(
            @RequestParam Long routeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Trip> trips = tripService.findByRouteIdAndDepartureTimeBetween(routeId, start, end);
        List<TripResponseDTO> tripResponseDTOs = tripMapper.toTripResponseList(trips);
        tripResponseDTOs = tripService.enrichTripResponseList(tripResponseDTOs);
        return ResponseEntity.ok(tripResponseDTOs);
    }

    @GetMapping("/available")
    public ResponseEntity<List<TripResponseDTO>> getAvailableTrips(@RequestParam(defaultValue = "1") Integer seats) {
        List<Trip> trips = tripService.findWithAvailableSeats(seats);
        List<TripResponseDTO> tripResponseDTOs = tripMapper.toTripResponseList(trips);
        tripResponseDTOs = tripService.enrichTripResponseList(tripResponseDTOs);
        return ResponseEntity.ok(tripResponseDTOs);
    }

    @PostMapping
    public ResponseEntity<TripResponseDTO> createTrip(@Valid @RequestBody TripDTO tripDTO) {
        Trip trip = tripService.createTrip(tripDTO);
        TripResponseDTO tripResponseDTO = tripMapper.toTripResponse(trip);
        tripResponseDTO = tripService.enrichTripResponse(tripResponseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(tripResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripResponseDTO> updateTrip(@PathVariable Long id, @Valid @RequestBody TripDTO tripDTO) {
        Trip updatedTrip = tripService.updateTrip(id, tripDTO);
        TripResponseDTO tripResponseDTO = tripMapper.toTripResponse(updatedTrip);
        tripResponseDTO = tripService.enrichTripResponse(tripResponseDTO);
        return ResponseEntity.ok(tripResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        tripService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}