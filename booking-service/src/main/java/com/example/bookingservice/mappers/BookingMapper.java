package com.example.bookingservice.mappers;/*
    @author User
    @project lab4
    @class BookingMapper
    @version 1.0.0
    @since 28.04.2025 - 18.17 
*/

import com.example.bookingservice.DTO.BookingDTO;
import com.example.bookingservice.DTO.BookingResponseDTO;
import com.example.bookingservice.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(target = "tickets", ignore = true)
    Booking toBooking(BookingDTO bookingDTO);

    @Mappings({
            //замість User, використовуємо тільки userId
            @Mapping(target = "userId", source = "userId"),
            @Mapping(target = "status", expression = "java(booking.getStatus().toString())")
    })
    BookingResponseDTO toBookingResponse(Booking booking);

    List<BookingResponseDTO> toBookingResponseList(List<Booking> bookings);
}

