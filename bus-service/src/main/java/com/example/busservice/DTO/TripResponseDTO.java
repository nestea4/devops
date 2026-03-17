package com.example.busservice.DTO;/*
    @author User
    @project lab4
    @class TripResponseDTO
    @version 1.0.0
    @since 28.04.2025 - 16.11 
*/

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
public class TripResponseDTO {
    private Long id;
    private Long routeId;
    private RouteDTO route;    // Додано для деталізації
    private Long busId;
    private BusDTO bus;        // Додано для деталізації
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal ticketPrice;
    private Integer availableSeats;
}
