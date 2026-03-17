package com.example.bookingservice.controller;/*
    @author User
    @project lab4
    @class BookingController
    @version 1.0.0
    @since 08.05.2025 - 02.05 
*/

import com.example.bookingservice.DTO.BookingCreateDTO;
import com.example.bookingservice.DTO.BookingResponseDTO;
import com.example.bookingservice.DTO.BookingUpdateDTO;
import com.example.bookingservice.mappers.BookingMapper;
import com.example.bookingservice.model.Booking;
import com.example.bookingservice.service.BookingEventProducer;
import com.example.bookingservice.service.BookingService;
import com.example.lab4.BookingEvent;
import com.example.userservice.DTO.UserDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
@Validated
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;
    private final RestTemplate restTemplate;
    private final BookingEventProducer bookingEventProducer;

    @Value("${service.user.url}")
    private String userServiceUrl;

    @Autowired
    public BookingController(BookingService bookingService,
                             BookingMapper bookingMapper,
                             RestTemplate restTemplate,
                             BookingEventProducer bookingEventProducer) {
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
        this.restTemplate = restTemplate;
        this.bookingEventProducer = bookingEventProducer;
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        List<Booking> bookings = bookingService.findAll();
        List<BookingResponseDTO> bookingResponses = bookingMapper.toBookingResponseList(bookings);
        return ResponseEntity.ok(enrichBookingResponses(bookingResponses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.findById(id);
        BookingResponseDTO response = bookingMapper.toBookingResponse(booking);
        return ResponseEntity.ok(enrichBookingResponse(response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByUserId(@PathVariable Long userId) {
        List<Booking> bookings = bookingService.findByUserId(userId);
        List<BookingResponseDTO> bookingResponses = bookingMapper.toBookingResponseList(bookings);
        return ResponseEntity.ok(enrichBookingResponses(bookingResponses));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByStatus(@PathVariable String status) {
        try {
            Booking.BookingStatus bookingStatus = Booking.BookingStatus.valueOf(status);
            List<Booking> bookings = bookingService.findByStatus(bookingStatus);
            List<BookingResponseDTO> bookingResponses = bookingMapper.toBookingResponseList(bookings);
            return ResponseEntity.ok(enrichBookingResponses(bookingResponses));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid booking status: " + status);
        }
    }

    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingCreateDTO bookingCreateDTO) {
        Booking savedBooking = bookingService.createBookingFromDto(bookingCreateDTO);
        BookingResponseDTO response = bookingMapper.toBookingResponse(savedBooking);

        BookingEvent bookingEvent = createBookingEvent(savedBooking, response, "CREATED");
        bookingEventProducer.sendBookingEvent(bookingEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(enrichBookingResponse(response));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BookingResponseDTO> updateBookingStatus(
            @PathVariable Long id,
            @Valid @RequestBody BookingUpdateDTO bookingUpdateDTO) {
        try {
            Booking.BookingStatus newStatus = Booking.BookingStatus.valueOf(bookingUpdateDTO.getStatus());
            Booking updatedBooking = bookingService.updateBookingStatus(id, newStatus);

            BookingResponseDTO response = bookingMapper.toBookingResponse(updatedBooking);

            BookingEvent bookingEvent = createBookingEvent(updatedBooking, response, "UPDATED");
            bookingEventProducer.sendBookingEvent(bookingEvent);

            return ResponseEntity.ok(enrichBookingResponse(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
//        bookingService.deleteById(id);
//
//        return ResponseEntity.noContent().build();

        Booking booking = bookingService.findById(id);
        BookingResponseDTO response = bookingMapper.toBookingResponse(booking);

        bookingService.deleteById(id);

        BookingEvent bookingEvent = createBookingEvent(booking, response, "CANCELLED");
        bookingEventProducer.sendBookingEvent(bookingEvent);

        return ResponseEntity.noContent().build();
    }

    private BookingResponseDTO enrichBookingResponse(BookingResponseDTO bookingResponse) {
        try {
            ResponseEntity<UserDTO> userResponse = restTemplate.getForEntity(
                    userServiceUrl + "/api/users/" + bookingResponse.getUserId(), UserDTO.class);

            if (userResponse.getBody() != null) {
                UserDTO user = userResponse.getBody();
                bookingResponse.setUserFullName(user.getFirstName() + " " + user.getLastName());
                bookingResponse.setUserEmail(user.getEmail());
            }
        } catch (RestClientException e) {
            bookingResponse.setUserFullName("Unknown");
            bookingResponse.setUserEmail("Unknown");
        }
        return bookingResponse;
    }

    private List<BookingResponseDTO> enrichBookingResponses(List<BookingResponseDTO> bookingResponses) {
        return bookingResponses.stream()
                .map(this::enrichBookingResponse)
                .collect(Collectors.toList());
    }

    private BookingEvent createBookingEvent(Booking booking, BookingResponseDTO response, String eventType) {
        return BookingEvent.builder()
                .eventType(eventType)
                .bookingId(booking.getId())
                .userId(booking.getUserId())
                .status(booking.getStatus().name())
                .userEmail(response.getUserEmail())
                .userFullName(response.getUserFullName())
                .totalAmount(booking.getTotalAmount())
                .ticketIds(booking.getTickets().stream().map(ticket -> ticket.getId()).collect(Collectors.toList()))
                .timestamp(LocalDateTime.now())
                .build();
    }
}
