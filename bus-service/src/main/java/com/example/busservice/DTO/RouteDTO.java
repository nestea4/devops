package com.example.busservice.DTO;/*
    @author User
    @project lab4
    @class RouteDTO
    @version 1.0.0
    @since 28.04.2025 - 16.08 
*/

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteDTO {
    private Long id;

    @NotBlank(message = "Departure city cannot be empty")
    private String departureCity;

    @NotBlank(message = "Arrival city cannot be empty")
    private String arrivalCity;

    @Positive(message = "Distance must be positive")
    private Double distance;
}
