package com.example.busservice.model;/*
    @author User
    @project lab4
    @class Trip
    @version 1.0.0
    @since 28.04.2025 - 15.43 
*/

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trips")
@Data
@NoArgsConstructor
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //
    @Column(name = "route_id", nullable = false)
    private Long routeId;

    @Column(name = "bus_id", nullable = false)
    private Long busId;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Column(nullable = false)
    private BigDecimal ticketPrice;

    @Column(nullable = false)
    private Integer availableSeats;

    // private List<Ticket> tickets;
}
