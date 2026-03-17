package com.example.bookingservice.controller;/*
    @author User
    @project lab4
    @class KafkaTestController
    @version 1.0.0
    @since 13.05.2025 - 00.34 
*/

import com.example.bookingservice.service.BookingEventProducer;
import com.example.lab4.BookingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;

@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
public class KafkaTestController {

    private final BookingEventProducer bookingEventProducer;

    @PostMapping("/send")
    public ResponseEntity<String> sendKafkaMessage(@RequestBody BookingEvent bookingEvent) {
        // Set timestamp if not provided
        if (bookingEvent.getTimestamp() == null) {
            bookingEvent.setTimestamp(LocalDateTime.now());
        }

        bookingEventProducer.sendBookingEvent(bookingEvent);
        return ResponseEntity.ok("Message sent to Kafka topic");
    }

    @GetMapping("/test")
    public ResponseEntity<String> testSimpleMessage(
            @RequestParam(defaultValue = "TEST") String eventType,
            @RequestParam(defaultValue = "1") Long bookingId,
            @RequestParam(defaultValue = "1") Long userId) {

        BookingEvent event = BookingEvent.builder()
                .eventType(eventType)
                .bookingId(bookingId)
                .userId(userId)
                .userEmail("test@example.com")
                .userFullName("Test User")
                .status("PENDING")
                .ticketIds(Collections.singletonList(1L))
                .timestamp(LocalDateTime.now())
                .build();

        bookingEventProducer.sendBookingEvent(event);
        return ResponseEntity.ok("Test message sent to Kafka topic");
    }
}
