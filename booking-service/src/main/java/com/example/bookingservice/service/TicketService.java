package com.example.bookingservice.service;/*
    @author User
    @project lab4
    @class TicketService
    @version 1.0.0
    @since 28.04.2025 - 18.26 
*/

import com.example.bookingservice.exeption.DuplicateResourceException;
import com.example.bookingservice.exeption.InvalidStateException;
import com.example.bookingservice.exeption.ResourceNotFoundException;
import com.example.bookingservice.model.Ticket;
import com.example.bookingservice.repository.TicketRepository;
import com.example.busservice.DTO.BusDTO;
import com.example.busservice.DTO.TripDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final RestTemplate restTemplate;

    @Value("${service.bus.url}")
    private String busServiceUrl;

    @Autowired
    public TicketService(TicketRepository ticketRepository, RestTemplate restTemplate) {
        this.ticketRepository = ticketRepository;
        this.restTemplate = restTemplate;
    }

    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> findById(Long id) {
        return ticketRepository.findById(id);
    }

    public List<Ticket> findByTripId(Long tripId) {
        // Validate that the trip exists
        try {
            ResponseEntity<TripDTO> response = restTemplate.getForEntity(
                    busServiceUrl + "/api/trips/" + tripId, TripDTO.class);
            // If trip doesn't exist, the bus-service will return 404
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Trip not found with id: " + tripId);
        }

        return ticketRepository.findByTripId(tripId);
    }

    public List<Ticket> findByBookingId(Long bookingId) {
        return ticketRepository.findByBookingId(bookingId);
    }

    public List<Ticket> findByStatus(Ticket.TicketStatus status) {
        return ticketRepository.findByStatus(status);
    }

    public List<Ticket> findAvailableTicketsForTrip(Long tripId) {
        // Validate that the trip exists
        try {
            ResponseEntity<TripDTO> response = restTemplate.getForEntity(
                    busServiceUrl + "/api/trips/" + tripId, TripDTO.class);
            // If trip doesn't exist, the bus-service will return 404
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Trip not found with id: " + tripId);
        }

        return ticketRepository.findByTripIdAndStatus(tripId, Ticket.TicketStatus.AVAILABLE);
    }

    @Transactional
    public Ticket save(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket createTicketForTrip(Long tripId, Integer seatNumber) {
        // Get trip from bus-service
        TripDTO trip;
        try {
            ResponseEntity<TripDTO> response = restTemplate.getForEntity(
                    busServiceUrl + "/api/trips/" + tripId, TripDTO.class);
            trip = response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Trip not found with id: " + tripId);
        }

        if (trip == null) {
            throw new ResourceNotFoundException("Trip not found with id: " + tripId);
        }

        // Get bus details from bus-service
        BusDTO bus;
        try {
            ResponseEntity<BusDTO> response = restTemplate.getForEntity(
                    busServiceUrl + "/api/buses/" + trip.getBusId(), BusDTO.class);
            bus = response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Bus not found for trip id: " + tripId);
        }

        if (bus == null) {
            throw new ResourceNotFoundException("Bus not found for trip id: " + tripId);
        }

        // Validate seat number
        if (seatNumber <= 0 || seatNumber > bus.getTotalSeats()) {
            throw new InvalidStateException("Invalid seat number: " + seatNumber +
                    ". Total seats available: " + bus.getTotalSeats());
        }

        // Check if ticket already exists for this seat
        Optional<Ticket> existingTicket = ticketRepository.findByTripIdAndSeatNumber(tripId, seatNumber);
        if (existingTicket.isPresent()) {
            throw new DuplicateResourceException("Ticket already exists for trip id: " + tripId +
                    " with seat number: " + seatNumber);
        }

        // Create new ticket
        Ticket ticket = new Ticket();
        ticket.setTripId(trip.getId());
        ticket.setSeatNumber(seatNumber);
        ticket.setPrice(trip.getTicketPrice());
        ticket.setStatus(Ticket.TicketStatus.AVAILABLE);

        return ticketRepository.save(ticket);
    }

    @Transactional
    public void updateTicketStatus(Long id, Ticket.TicketStatus status) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            Ticket.TicketStatus oldStatus = ticket.getStatus();

            // If status changes from available to booked/paid
            if (oldStatus == Ticket.TicketStatus.AVAILABLE &&
                    (status == Ticket.TicketStatus.BOOKED || status == Ticket.TicketStatus.PAID)) {
                // Decrease available seats in the trip
                updateTripAvailableSeats(ticket.getTripId(), -1);
            }
            // If status changes to available from booked/paid
            else if ((oldStatus == Ticket.TicketStatus.BOOKED || oldStatus == Ticket.TicketStatus.PAID) &&
                    status == Ticket.TicketStatus.AVAILABLE) {
                // Increase available seats in the trip
                updateTripAvailableSeats(ticket.getTripId(), 1);
            }

            ticket.setStatus(status);
            ticketRepository.save(ticket);
        } else {
            throw new ResourceNotFoundException("Ticket not found with id: " + id);
        }
    }

    @Transactional
    public List<Ticket> generateTicketsForTrip(TripDTO trip) {
        List<Ticket> tickets = new ArrayList<>();

        // Get bus details
        BusDTO bus;
        try {
            ResponseEntity<BusDTO> response = restTemplate.getForEntity(
                    busServiceUrl + "/api/buses/" + trip.getBusId(), BusDTO.class);
            bus = response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Bus not found with id: " + trip.getBusId());
        }

        if (bus == null) {
            throw new ResourceNotFoundException("Bus not found with id: " + trip.getBusId());
        }

        // Create tickets for each seat in the bus
        for (int i = 1; i <= bus.getTotalSeats(); i++) {
            Ticket ticket = new Ticket();
            ticket.setTripId(trip.getId());
            ticket.setSeatNumber(i);
            ticket.setPrice(trip.getTicketPrice());
            ticket.setStatus(Ticket.TicketStatus.AVAILABLE);
            tickets.add(ticketRepository.save(ticket));
        }

        return tickets;
    }

    @Transactional
    public void deleteById(Long id) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();

            // If ticket is booked or paid, increase available seats in trip
            if (ticket.getStatus() == Ticket.TicketStatus.BOOKED ||
                    ticket.getStatus() == Ticket.TicketStatus.PAID) {
                updateTripAvailableSeats(ticket.getTripId(), 1);
            }

            ticketRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Ticket not found with id: " + id);
        }
    }

    // Method to update available seats in the trip
    private void updateTripAvailableSeats(Long tripId, int delta) {
        try {
            // Call bus-service to update available seats
            restTemplate.put(
                    busServiceUrl + "/api/trips/" + tripId + "/seats?delta=" + delta,
                    null
            );
        } catch (RestClientException e) {
            // Log error but continue processing
            // In a production environment, consider using Circuit Breaker pattern
        }
    }
}