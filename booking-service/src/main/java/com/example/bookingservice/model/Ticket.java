package com.example.bookingservice.model;/*
    @author User
    @project lab4
    @class Ticket
    @version 1.0.0
    @since 28.04.2025 - 18.14 
*/

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //тут
    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Column(nullable = false)
    private Integer seatNumber;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.AVAILABLE;

    public enum TicketStatus {
        AVAILABLE,
        BOOKED,
        PAID,
        CANCELED
    }
}
