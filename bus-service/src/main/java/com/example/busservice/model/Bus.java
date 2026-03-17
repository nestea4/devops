package com.example.busservice.model;/*
    @author User
    @project lab4
    @class Bus
    @version 1.0.0
    @since 28.04.2025 - 15.41 
*/

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "buses")
@Data
@NoArgsConstructor
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BusType type;

    @Column(nullable = false)
    private Integer totalSeats;

    @Column
    private String make;

    @Column
    private String registrationNumber;

    // private List<Trip> trips;

    public enum BusType {
        SMALL,   //Малий
        MEDIUM,  //Середній
        LARGE    //Великий
    }
}
