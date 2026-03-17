package com.example.busservice.DTO;/*
    @author User
    @project lab4
    @class RouteResponseDTO
    @version 1.0.0
    @since 28.04.2025 - 16.09 
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponseDTO {
    private Long id;
    private String departureCity;
    private String arrivalCity;
    private Double distance;
    // Remove trips list as it's now in a different service
}
