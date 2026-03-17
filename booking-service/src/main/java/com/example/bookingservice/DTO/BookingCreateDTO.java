package com.example.bookingservice.DTO;/*
    @author User
    @project lab4
    @class BookingCreateDTO
    @version 1.0.0
    @since 28.04.2025 - 18.16 
*/

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCreateDTO {
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotEmpty(message = "Booking must contain at least one ticket")
    private List<Long> ticketIds;
}
