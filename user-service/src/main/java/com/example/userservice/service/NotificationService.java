package com.example.userservice.service;/*
    @author User
    @project lab4
    @class NotificationService
    @version 1.0.0
    @since 13.05.2025 - 00.34 
*/

import com.example.lab4.BookingEvent;
import com.example.userservice.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void sendBookingConfirmation(User user, BookingEvent bookingEvent) {
        log.info("Sending booking confirmation to user: {}, Email: {}, BookingID: {}, Total Amount: {}",
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                bookingEvent.getBookingId(),
                bookingEvent.getTotalAmount());

    }

    public void sendBookingStatusUpdate(User user, BookingEvent bookingEvent) {
        log.info("Sending booking status update to user: {}, Email: {}, BookingID: {}, New Status: {}",
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                bookingEvent.getBookingId(),
                bookingEvent.getStatus());

    }

    public void sendBookingCancellation(User user, BookingEvent bookingEvent) {
        log.info("Sending booking cancellation to user: {}, Email: {}, BookingID: {}",
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                bookingEvent.getBookingId());
    }
}
