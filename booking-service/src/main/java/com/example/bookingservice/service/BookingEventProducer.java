package com.example.bookingservice.service;/*
    @author User
    @project lab4
    @class BookingEventProducer
    @version 1.0.0
    @since 13.05.2025 - 00.19 
*/

import com.example.bookingservice.config.KafkaProducerConfig;
import com.example.lab4.BookingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendBookingEvent(BookingEvent bookingEvent) {
        String key = UUID.randomUUID().toString();
        bookingEvent.setEventId(key);

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaProducerConfig.BOOKING_TOPIC,
                key,
                bookingEvent
        );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent booking event [key={}, eventType={}, bookingId={}] with offset=[{}]",
                        key,
                        bookingEvent.getEventType(),
                        bookingEvent.getBookingId(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send booking event [key={}, eventType={}, bookingId={}] due to : {}",
                        key,
                        bookingEvent.getEventType(),
                        bookingEvent.getBookingId(),
                        ex.getMessage());
            }
        });
    }
}
