package com.example.lab4;/*
    @author User
    @project lab4
    @class BookingEvent
    @version 1.0.0
    @since 11.05.2025 - 23.43 
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
public class BookingEvent {
//    private String eventId;
//    private String eventType; // CREATED, UPDATED, CANCELLED
//    private Long bookingId;
//    private Long userId;
//    private String status;
//    private LocalDateTime timestamp;
//
//    private String userEmail;
//    private String userFullName;
//    private BigDecimal totalAmount;
//    private List<Long> ticketIds;

    private String eventId;
    private String eventType; //CREATED,UPDATED,CANCELLED
    private Long bookingId;
    private Long userId;
    private String status;
    private LocalDateTime timestamp;

    private String userEmail;
    private String userFullName;
    private BigDecimal totalAmount;
    private List<Long> ticketIds;
}
