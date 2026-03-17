package com.example.bookingservice.mappers;/*
    @author User
    @project lab4
    @class TicketMapper
    @version 1.0.0
    @since 28.04.2025 - 18.22 
*/


import com.example.bookingservice.DTO.TicketDTO;
import com.example.bookingservice.DTO.TicketResponseDTO;
import com.example.bookingservice.model.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

//@Mapper(componentModel = "spring")
//public interface TicketMapper {
//    TicketMapper INSTANCE = Mappers.getMapper(TicketMapper.class);
//
//    @Mappings({
//            //@Mapping(target = "trip", ignore = true),
//            @Mapping(target = "booking", ignore = true),
//            @Mapping(target = "status", expression = "java(ticketDTO.getStatus() != null ? Ticket.TicketStatus.valueOf(ticketDTO.getStatus()) : null)")
//    })
//    Ticket toTicket(TicketDTO ticketDTO);
//
//    @Mappings({
//            @Mapping(target = "tripInfo", expression = "java(ticket.getTripId().getRoute().getDepartureCity() + \" - \" + ticket.getTripId().getRoute().getArrivalCity() + \" (\" + ticket.getTripId().getDepartureTime() + \")\")"),
//            @Mapping(target = "status", expression = "java(ticket.getStatus().toString())")
//    })
//    TicketResponseDTO toTicketResponse(Ticket ticket);
//
//    List<TicketResponseDTO> toTicketResponseList(List<Ticket> tickets);
//
//    @Mappings({
//            @Mapping(target = "id", ignore = true),
//           // @Mapping(target = "trip", ignore = true),
//            @Mapping(target = "booking", ignore = true),
//            @Mapping(target = "status", expression = "java(ticketDTO.getStatus() != null ? Ticket.TicketStatus.valueOf(ticketDTO.getStatus()) : ticket.getStatus())")
//    })
//    void updateTicketFromDto(TicketDTO ticketDTO, @MappingTarget Ticket ticket);
//}


@Mapper(componentModel = "spring")
public interface TicketMapper {
    TicketMapper INSTANCE = Mappers.getMapper(TicketMapper.class);

    @Mappings({
            @Mapping(target = "booking", ignore = true),
            @Mapping(target = "status", expression = "java(ticketDTO.getStatus() != null ? Ticket.TicketStatus.valueOf(ticketDTO.getStatus()) : null)")
    })
    Ticket toTicket(TicketDTO ticketDTO);

    @Mappings({
            @Mapping(target = "tripId", source = "tripId"),
            @Mapping(target = "status", expression = "java(ticket.getStatus().toString())")
    })
    TicketResponseDTO toTicketResponse(Ticket ticket);

    List<TicketResponseDTO> toTicketResponseList(List<Ticket> tickets);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "booking", ignore = true),
            @Mapping(target = "status", expression = "java(ticketDTO.getStatus() != null ? Ticket.TicketStatus.valueOf(ticketDTO.getStatus()) : ticket.getStatus())")
    })
    void updateTicketFromDto(TicketDTO ticketDTO, @MappingTarget Ticket ticket);
}