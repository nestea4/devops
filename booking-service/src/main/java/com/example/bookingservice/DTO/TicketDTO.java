package com.example.bookingservice.DTO;/*
    @author User
    @project lab4
    @class TicketDTO
    @version 1.0.0
    @since 28.04.2025 - 18.16 
*/

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDTO {
    private Long id;

    @NotNull(message = "Trip ID cannot be null")
    private Long tripId;

    @NotNull(message = "Seat number cannot be null")
    @Positive(message = "Seat number must be positive")
    private Integer seatNumber;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    private Long bookingId;

    private String status;
}
