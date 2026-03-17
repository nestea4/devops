package com.example.bookingservice.DTO;/*
    @author User
    @project lab4
    @class BookingUpdateDTO
    @version 1.0.0
    @since 28.04.2025 - 18.21 
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingUpdateDTO {
    private String status;
}
