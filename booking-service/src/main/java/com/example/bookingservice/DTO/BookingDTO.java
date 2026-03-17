package com.example.bookingservice.DTO;/*
    @author User
    @project lab4
    @class BookingDTO
    @version 1.0.0
    @since 28.04.2025 - 18.16 
*/

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDTO {
    private Long id;

    @NotNull(message = "User ID cannot be null")
    private Long user;

    private LocalDateTime bookingDateTime;

    private BigDecimal totalAmount;

    private String status;

    @NotEmpty(message = "Booking must contain at least one ticket")
    private List<Long> ticketIds;
}