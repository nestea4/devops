package com.example.busservice.DTO;/*
    @author User
    @project lab4
    @class BusDTO
    @version 1.0.0
    @since 28.04.2025 - 15.46 
*/

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusDTO {
    private Long id;

    @NotBlank(message = "Bus number cannot be empty")
    private String number;

    @NotNull(message = "Bus type cannot be null")
    private String type;

    @NotNull(message = "Total seats cannot be null")
    @Min(value = 1, message = "Total seats must be at least 1")
    private Integer totalSeats;

    private String make;

    @Pattern(regexp = "^[A-Z]{2}\\d{4}[A-Z]{2}$", message = "Registration number must be in format XX0000XX")
    private String registrationNumber;
}

