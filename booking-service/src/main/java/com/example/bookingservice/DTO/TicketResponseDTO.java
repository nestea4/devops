package com.example.bookingservice.DTO;/*
    @author User
    @project lab4
    @class TicketResponseDTO
    @version 1.0.0
    @since 28.04.2025 - 18.16 
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponseDTO {
    private Long id;
    private Long tripId;
    private String tripInfo;
    private Integer seatNumber;
    private BigDecimal price;
    private String status;
}