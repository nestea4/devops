package com.example.busservice.mappers;/*
    @author User
    @project lab4
    @class BusMapper
    @version 1.0.0
    @since 28.04.2025 - 15.50 
*/

import com.example.busservice.DTO.BusDTO;
import com.example.busservice.DTO.BusResponseDTO;
import com.example.busservice.model.Bus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BusMapper {
    BusMapper INSTANCE = Mappers.getMapper(BusMapper.class);

    @Mapping(target = "type", expression = "java(busDTO.getType() != null ? Bus.BusType.valueOf(busDTO.getType()) : null)")
    Bus toBus(BusDTO busDTO);

    @Mapping(target = "type", expression = "java(bus.getType().toString())")
    BusResponseDTO toBusResponse(Bus bus);

    List<BusResponseDTO> toBusResponseList(List<Bus> buses);

    @Mapping(target = "type", expression = "java(busDTO.getType() != null ? Bus.BusType.valueOf(busDTO.getType()) : bus.getType())")
    void updateBusFromDto(BusDTO busDTO, @MappingTarget Bus bus);
}