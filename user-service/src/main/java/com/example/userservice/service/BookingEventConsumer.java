package com.example.userservice.service;/*
    @author User
    @project lab4
    @class BookingEventConsumer
    @version 1.0.0
    @since 13.05.2025 - 00.32 
*/

import com.example.lab4.BookingEvent;
import com.example.userservice.config.KafkaConsumerConfig;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingEventConsumer {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @KafkaListener(
            topics = KafkaConsumerConfig.BOOKING_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(BookingEvent bookingEvent) {
        log.info("Received booking event: {}", bookingEvent);

        Optional<User> userOptional = userRepository.findById(bookingEvent.getUserId());

        if (userOptional.isEmpty()) {
            log.error("User not found for booking event: {}", bookingEvent);
            return;
        }

        User user = userOptional.get();

        switch (bookingEvent.getEventType()) {
            case "CREATED":
                log.info("Processing CREATED booking event: BookingID={}, UserID={}",
                        bookingEvent.getBookingId(), bookingEvent.getUserId());
                notificationService.sendBookingConfirmation(user, bookingEvent);
                break;

            case "UPDATED":
                log.info("Processing UPDATED booking event: BookingID={}, New status={}",
                        bookingEvent.getBookingId(), bookingEvent.getStatus());
                notificationService.sendBookingStatusUpdate(user, bookingEvent);
                break;

            case "CANCELLED":
                log.info("Processing CANCELLED booking event: BookingID={}",
                        bookingEvent.getBookingId());
                notificationService.sendBookingCancellation(user, bookingEvent);
                break;

            default:
                log.warn("Unknown event type: {}", bookingEvent.getEventType());
        }
    }
}
