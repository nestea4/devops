package com.example.bookingservice.repository;/*
    @author User
    @project lab4
    @class TicketRepository
    @version 1.0.0
    @since 28.04.2025 - 18.25 
*/

import com.example.bookingservice.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByTripId(Long tripId);
    List<Ticket> findByBookingId(Long bookingId);
    List<Ticket> findByStatus(Ticket.TicketStatus status);
    List<Ticket> findByTripIdAndStatus(Long tripId, Ticket.TicketStatus status);
    Optional<Ticket> findByTripIdAndSeatNumber(Long tripId, Integer seatNumber);
}
