package com.example.bookingservice.DTO;/*
    @author User
    @project lab4
    @class BokkingResponseDTO
    @version 1.0.0
    @since 28.04.2025 - 18.16 
*/

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
public class BookingResponseDTO {
    private Long id;
    private Long userId;

    private String userFullName;
    private String userEmail;
    private LocalDateTime bookingDateTime;
    private BigDecimal totalAmount;
    private String status;
    private List<TicketResponseDTO> tickets;
}
