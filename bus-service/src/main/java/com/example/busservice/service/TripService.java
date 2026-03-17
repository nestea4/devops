package com.example.busservice.service;/*
    @author User
    @project lab4
    @class TripService
    @version 1.0.0
    @since 28.04.2025 - 17.40 
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
import com.example.busservice.repository.BusRepository;
import com.example.busservice.repository.RouteRepository;
import com.example.busservice.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;
    private final BusService busService;
    private final RouteService routeService;
    private final RestTemplate restTemplate;

    @Value("${service.booking.url}")
    private String bookingServiceUrl;

    @Autowired
    public TripService(TripRepository tripRepository,
                       TripMapper tripMapper,
                       BusService busService,
                       RouteService routeService,
                       RestTemplate restTemplate) {
        this.tripRepository = tripRepository;
        this.tripMapper = tripMapper;
        this.busService = busService;
        this.routeService = routeService;
        this.restTemplate = restTemplate;
    }

    public List<Trip> findAll() {
        return tripRepository.findAll();
    }

    public Trip findById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + id));
    }

    public List<Trip> findByRouteId(Long routeId) {
        routeService.findById(routeId);

        List<Trip> trips = tripRepository.findByRouteId(routeId);
        if (trips.isEmpty()) {
            throw new ResourceNotFoundException("No trips found for route with id: " + routeId);
        }
        return trips;
    }

    public List<Trip> findByBusId(Long busId) {
        busService.findById(busId);

        List<Trip> trips = tripRepository.findByBusId(busId);
        if (trips.isEmpty()) {
            throw new ResourceNotFoundException("No trips found for bus with id: " + busId);
        }
        return trips;
    }

    public List<Trip> findByDepartureTimeBetween(LocalDateTime start, LocalDateTime end) {
        List<Trip> trips = tripRepository.findByDepartureTimeBetween(start, end);
        if (trips.isEmpty()) {
            throw new ResourceNotFoundException("No trips found between " + start + " and " + end);
        }
        return trips;
    }

    public List<Trip> findByRouteIdAndDepartureTimeBetween(Long routeId, LocalDateTime start, LocalDateTime end) {
        routeService.findById(routeId);

        List<Trip> trips = tripRepository.findByRouteIdAndDepartureTimeBetween(routeId, start, end);
        if (trips.isEmpty()) {
            throw new ResourceNotFoundException("No trips found for route " + routeId + " between " + start + " and " + end);
        }
        return trips;
    }

    public List<Trip> findWithAvailableSeats(Integer minSeats) {
        List<Trip> trips = tripRepository.findByAvailableSeatsGreaterThanEqual(minSeats);
        if (trips.isEmpty()) {
            throw new ResourceNotFoundException("No trips found with at least " + minSeats + " available seats");
        }
        return trips;
    }

    @Transactional
    public Trip createTrip(TripDTO tripDTO) {
        Bus bus = busService.findById(tripDTO.getBusId());
        Route route = routeService.findById(tripDTO.getRouteId());

        Trip trip = tripMapper.toTrip(tripDTO);

        if (trip.getAvailableSeats() == null) {
            trip.setAvailableSeats(bus.getTotalSeats());
        }

        Trip savedTrip = tripRepository.save(trip);

        notifyBookingServiceAboutCreation(savedTrip);

        return savedTrip;
    }

    @Transactional
    public Trip updateTrip(Long id, TripDTO tripDTO) {
        Trip existingTrip = findById(id);

        busService.findById(tripDTO.getBusId());
        routeService.findById(tripDTO.getRouteId());

        tripMapper.updateTripFromDto(tripDTO, existingTrip);

        Trip updatedTrip = tripRepository.save(existingTrip);

        notifyBookingServiceAboutUpdate(updatedTrip);

        return updatedTrip;
    }

    public void deleteById(Long id) {
        Trip trip = findById(id);

        notifyBookingServiceAboutDeletion(id);

        tripRepository.deleteById(id);
    }

    public TripResponseDTO enrichTripResponse(TripResponseDTO tripResponse) {
        try {
            Bus bus = busService.findById(tripResponse.getBusId());
            BusDTO busDTO = convertBusToDTO(bus);
            tripResponse.setBus(busDTO);

            Route route = routeService.findById(tripResponse.getRouteId());
            RouteDTO routeDTO = convertRouteToDTO(route);
            tripResponse.setRoute(routeDTO);

            updateAvailableSeatsFromBookingService(tripResponse);

            return tripResponse;
        } catch (Exception e) {

            return tripResponse;
        }
    }

    public List<TripResponseDTO> enrichTripResponseList(List<TripResponseDTO> tripResponses) {
        return tripResponses.stream()
                .map(this::enrichTripResponse)
                .collect(Collectors.toList());
    }

    private void notifyBookingServiceAboutCreation(Trip trip) {
        try {
            TripDTO tripDTO = tripMapper.toTripDTO(trip);
            restTemplate.postForEntity(
                    bookingServiceUrl + "/api/bookings/trip-created",
                    tripDTO,
                    Void.class
            );
        } catch (RestClientException e) {
        }
    }

    private void notifyBookingServiceAboutUpdate(Trip trip) {
        try {
            TripDTO tripDTO = tripMapper.toTripDTO(trip);
            restTemplate.put(
                    bookingServiceUrl + "/api/bookings/trip-updated",
                    tripDTO
            );
        } catch (RestClientException e) {

        }
    }

    private void notifyBookingServiceAboutDeletion(Long tripId) {
        try {
            restTemplate.delete(bookingServiceUrl + "/api/bookings/trip-deleted/" + tripId);
        } catch (RestClientException e) {

        }
    }

    private void updateAvailableSeatsFromBookingService(TripResponseDTO tripResponse) {
        try {
            Integer bookedSeats = restTemplate.getForObject(
                    bookingServiceUrl + "/api/bookings/booked-seats-count/" + tripResponse.getId(),
                    Integer.class
            );

            if (bookedSeats != null) {
                Trip trip = findById(tripResponse.getId());
                Bus bus = busService.findById(trip.getBusId());
                int availableSeats = bus.getTotalSeats() - bookedSeats;

                trip.setAvailableSeats(availableSeats);
                tripRepository.save(trip);
                tripResponse.setAvailableSeats(availableSeats);
            }
        } catch (RestClientException e) {
        }
    }

    private BusDTO convertBusToDTO(Bus bus) {
        return BusDTO.builder()
                .id(bus.getId())
                .registrationNumber(bus.getRegistrationNumber())
                .totalSeats(bus.getTotalSeats())
                .build();
    }

    private RouteDTO convertRouteToDTO(Route route) {
        return RouteDTO.builder()
                .id(route.getId())
                .departureCity(route.getDepartureCity())
                .arrivalCity(route.getArrivalCity())
                .distance(route.getDistance())
                .build();
    }
}