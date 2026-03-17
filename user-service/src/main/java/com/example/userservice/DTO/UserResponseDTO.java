package com.example.userservice.DTO;/*
    @author User
    @project lab4
    @class UserResponseDTO
    @version 1.0.0
    @since 28.04.2025 - 15.22 
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String fullName;
}
