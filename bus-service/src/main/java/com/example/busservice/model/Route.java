package com.example.busservice.model;/*
    @author User
    @project lab4
    @class Route
    @version 1.0.0
    @since 28.04.2025 - 15.42 
*/

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "routes")
@Data
@NoArgsConstructor
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String departureCity;

    @Column(nullable = false)
    private String arrivalCity;

    @Column
    private Double distance;

    // private List<Trip> trips;
}
