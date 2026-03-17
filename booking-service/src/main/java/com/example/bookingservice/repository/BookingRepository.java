package com.example.bookingservice.repository;/*
    @author User
    @project lab4
    @class BookingRepository
    @version 1.0.0
    @since 28.04.2025 - 18.24 
*/

import com.example.bookingservice.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByStatus(Booking.BookingStatus status);
}
