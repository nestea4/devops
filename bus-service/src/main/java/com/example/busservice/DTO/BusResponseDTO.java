package com.example.busservice.DTO;/*
    @author User
    @project lab4
    @class BusResponseDTO
    @version 1.0.0
    @since 28.04.2025 - 15.46 
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusResponseDTO {
    private Long id;
    private String number;
    private String type;
    private Integer totalSeats;
    private String make;
    private String registrationNumber;
}
