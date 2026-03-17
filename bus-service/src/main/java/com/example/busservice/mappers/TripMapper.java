package com.example.busservice.mappers;/*
    @author User
    @project lab4
    @class TripMapper
    @version 1.0.0
    @since 28.04.2025 - 17.41 
*/

import com.example.busservice.DTO.TripDTO;
import com.example.busservice.DTO.TripResponseDTO;
import com.example.busservice.model.Trip;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TripMapper {
    TripMapper INSTANCE = Mappers.getMapper(TripMapper.class);

    @Mappings({
            @Mapping(source = "routeId", target = "routeId"),
            @Mapping(source = "busId", target = "busId")
    })
    Trip toTrip(TripDTO tripDTO);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "routeId", target = "routeId"),
            @Mapping(source = "busId", target = "busId"),
            @Mapping(source = "departureTime", target = "departureTime"),
            @Mapping(source = "arrivalTime", target = "arrivalTime"),
            @Mapping(source = "ticketPrice", target = "ticketPrice"),
            @Mapping(source = "availableSeats", target = "availableSeats"),
            //@Mapping(target = "durationMinutes", expression = "java(java.time.Duration.between(trip.getDepartureTime(), trip.getArrivalTime()).toMinutes())")
    })
    TripResponseDTO toTripResponse(Trip trip);

    List<TripResponseDTO> toTripResponseList(List<Trip> trips);

    @Mappings({
            @Mapping(target = "id", ignore = true)
    })
    void updateTripFromDto(TripDTO tripDTO, @MappingTarget Trip trip);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "routeId", target = "routeId"),
            @Mapping(source = "busId", target = "busId"),
            @Mapping(source = "departureTime", target = "departureTime"),
            @Mapping(source = "arrivalTime", target = "arrivalTime"),
            @Mapping(source = "ticketPrice", target = "ticketPrice"),
            @Mapping(source = "availableSeats", target = "availableSeats")
    })
    TripDTO toTripDTO(Trip trip);
}