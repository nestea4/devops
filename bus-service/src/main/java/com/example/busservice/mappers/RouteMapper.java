package com.example.busservice.mappers;/*
    @author User
    @project lab4
    @class RouteMapper
    @version 1.0.0
    @since 28.04.2025 - 17.56 
*/

import com.example.busservice.DTO.RouteDTO;
import com.example.busservice.DTO.RouteResponseDTO;
import com.example.busservice.model.Route;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RouteMapper {
    RouteMapper INSTANCE = Mappers.getMapper(RouteMapper.class);

    Route toRoute(RouteDTO routeDTO);

    RouteResponseDTO toRouteResponse(Route route);

    List<RouteResponseDTO> toRouteResponseList(List<Route> routes);

    @Mapping(target = "id", ignore = true)
    void updateRouteFromDto(RouteDTO routeDTO, @MappingTarget Route route);
}