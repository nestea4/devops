package com.example.busservice.DTO;/*
    @author User
    @project lab4
    @class TripDTO
    @version 1.0.0
    @since 28.04.2025 - 16.10 
*/

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripDTO {
    private Long id;

    @NotNull(message = "Route ID cannot be null")
    private Long routeId;

    @NotNull(message = "Bus ID cannot be null")
    private Long busId;

    @NotNull(message = "Departure time cannot be null")
    @Future(message = "Departure time must be in the future")
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival time cannot be null")
    @Future(message = "Arrival time must be in the future")
    private LocalDateTime arrivalTime;

    @NotNull(message = "Ticket price cannot be null")
    @Positive(message = "Ticket price must be positive")
    private BigDecimal ticketPrice;

    @NotNull(message = "Available seats cannot be null")
    @Positive(message = "Available seats must be positive")
    private Integer availableSeats;
}
