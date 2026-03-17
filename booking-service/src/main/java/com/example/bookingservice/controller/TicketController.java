package com.example.bookingservice.controller;/*
    @author User
    @project lab4
    @class TicketController
    @version 1.0.0
    @since 08.05.2025 - 02.11
*/

import com.example.bookingservice.DTO.TicketResponseDTO;
import com.example.bookingservice.exeption.ResourceNotFoundException;
import com.example.bookingservice.mappers.TicketMapper;
import com.example.bookingservice.model.Ticket;
import com.example.bookingservice.service.TicketService;
import com.example.busservice.DTO.RouteDTO;
import com.example.busservice.DTO.TripDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final RestTemplate restTemplate;

    @Value("${service.bus.url}")
    private String busServiceUrl;

    @Autowired
    public TicketController(TicketService ticketService,
                            TicketMapper ticketMapper,
                            RestTemplate restTemplate) {
        this.ticketService = ticketService;
        this.ticketMapper = ticketMapper;
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> getAllTickets() {
        List<Ticket> tickets = ticketService.findAll();
        List<TicketResponseDTO> ticketResponseDTOs = ticketMapper.toTicketResponseList(tickets);
        return ResponseEntity.ok(enrichTicketResponses(ticketResponseDTOs, tickets));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> getTicketById(@PathVariable Long id) {
        Optional<Ticket> ticketOptional = ticketService.findById(id);
        return ticketOptional.map(ticket -> ResponseEntity.ok(enrichTicketResponse(ticketMapper.toTicketResponse(ticket), ticketOptional.get().getTripId())))
                .orElse(ResponseEntity.notFound().build());
    }

//    @GetMapping("/trip/{tripId}")
//    public ResponseEntity<List<TicketResponseDTO>> getTicketsByTripId(@PathVariable Long tripId) {
//        List<Ticket> tickets = ticketService.findByTripId(tripId);
//        List<TicketResponseDTO> ticketResponseDTOs = ticketMapper.toTicketResponseList(tickets);
//        return ResponseEntity.ok(enrichTicketResponses(ticketResponseDTOs, tickets));
//    }
//
//    @GetMapping("/booking/{bookingId}")
//    public ResponseEntity<List<TicketResponseDTO>> getTicketsByBookingId(@PathVariable Long bookingId) {
//        List<Ticket> tickets = ticketService.findByBookingId(bookingId);
//        List<TicketResponseDTO> ticketResponseDTOs = ticketMapper.toTicketResponseList(tickets);
//        return ResponseEntity.ok(enrichTicketResponses(ticketResponseDTOs, tickets));
//    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketResponseDTO>> getTicketsByStatus(@PathVariable String status) {
        try {
            Ticket.TicketStatus ticketStatus = Ticket.TicketStatus.valueOf(status);
            List<Ticket> tickets = ticketService.findByStatus(ticketStatus);
            List<TicketResponseDTO> ticketResponseDTOs = ticketMapper.toTicketResponseList(tickets);
            return ResponseEntity.ok(enrichTicketResponses(ticketResponseDTOs, tickets));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

//    @GetMapping("/trip/{tripId}/available")
//    public ResponseEntity<List<TicketResponseDTO>> getAvailableTicketsForTrip(@PathVariable Long tripId) {
//        List<Ticket> tickets = ticketService.findAvailableTicketsForTrip(tripId);
//        List<TicketResponseDTO> ticketResponseDTOs = ticketMapper.toTicketResponseList(tickets);
//        return ResponseEntity.ok(enrichTicketResponses(ticketResponseDTOs, tickets));
//    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TicketResponseDTO> updateTicketStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            Ticket.TicketStatus ticketStatus = Ticket.TicketStatus.valueOf(status.toUpperCase());
            ticketService.updateTicketStatus(id, ticketStatus);
            Optional<Ticket> updatedTicket = ticketService.findById(id);
            if (updatedTicket.isPresent()) {
                TicketResponseDTO response = ticketMapper.toTicketResponse(updatedTicket.get());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid ticket status: " + status);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    e.getMessage());
        }
    }

    @PostMapping("/generate-for-trip/{tripId}")
    public ResponseEntity<List<TicketResponseDTO>> generateTicketsForTrip(
            @PathVariable Long tripId) {
        try {
            ResponseEntity<TripDTO> response = restTemplate.getForEntity(
                    busServiceUrl + "/api/trips/" + tripId,
                    TripDTO.class);

            if (response.getBody() == null) {
                throw new ResourceNotFoundException("Trip not found with id: " + tripId);
            }

            List<Ticket> tickets = ticketService.generateTicketsForTrip(response.getBody());
            List<TicketResponseDTO> ticketResponseDTOs = ticketMapper.toTicketResponseList(tickets);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ticketResponseDTOs);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Trip not found with id: " + tripId);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        try {
            ticketService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    e.getMessage());
        }
    }

    private TicketResponseDTO enrichTicketResponse(TicketResponseDTO response, Long tripId) {
        try {
            ResponseEntity<TripDTO> tripResponse = restTemplate.getForEntity(
                    busServiceUrl + "/api/trips/" + tripId,
                    TripDTO.class);

            if (tripResponse.getBody() != null) {
                TripDTO tripDetails = tripResponse.getBody();

                ResponseEntity<RouteDTO> routeResponse = restTemplate.getForEntity(
                        busServiceUrl + "/api/routes/" + tripDetails.getRouteId(),
                        RouteDTO.class);

                if (routeResponse.getBody() != null) {
                    RouteDTO routeDetails = routeResponse.getBody();
                    response.setTripInfo(String.format("%s - %s (Departure: %s, Price: %s)",
                            routeDetails.getDepartureCity(),
                            routeDetails.getArrivalCity(),
                            tripDetails.getDepartureTime(),
                            tripDetails.getTicketPrice()));
                } else {
                    response.setTripInfo(String.format("Trip %d (Departure: %s, Price: %s)",
                            tripDetails.getId(),
                            tripDetails.getDepartureTime(),
                            tripDetails.getTicketPrice()));
                }
            }
        } catch (Exception e) {
            response.setTripInfo("Trip details unavailable");
        }
        return response;
    }

    private List<TicketResponseDTO> enrichTicketResponses(List<TicketResponseDTO> responses, List<Ticket> tickets) {
        for (int i = 0; i < responses.size(); i++) {
            Ticket ticket = tickets.get(i);
            enrichTicketResponse(responses.get(i), ticket.getTripId());
        }
        return responses;
    }
}