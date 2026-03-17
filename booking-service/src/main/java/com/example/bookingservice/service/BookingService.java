package com.example.bookingservice.service;/*
    @author User
    @project lab4
    @class BookingService
    @version 1.0.0
    @since 28.04.2025 - 18.26 
*/

import com.example.bookingservice.DTO.BookingCreateDTO;
import com.example.bookingservice.exeption.InvalidStateException;
import com.example.bookingservice.exeption.ResourceNotFoundException;
import com.example.bookingservice.model.Booking;
import com.example.bookingservice.model.Ticket;
import com.example.bookingservice.repository.BookingRepository;
import com.example.bookingservice.repository.TicketRepository;
import com.example.userservice.DTO.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.naming.ServiceUnavailableException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final RestTemplate restTemplate;

    @Value("${service.user.url}")
    private String userServiceUrl;

    @Value("${service.bus.url}")
    private String busServiceUrl;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          TicketRepository ticketRepository,
                          RestTemplate restTemplate) {
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.restTemplate = restTemplate;
    }

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
    }

    public List<Booking> findByUserId(Long userId) {
        //чи є юзер
        try {
            ResponseEntity<UserDTO> response = restTemplate.getForEntity(
                    userServiceUrl + "/api/users/" + userId, UserDTO.class);
            //404
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        List<Booking> bookings = bookingRepository.findByUserId(userId);
        if (bookings.isEmpty()) {
            throw new ResourceNotFoundException("No bookings found for user with id: " + userId);
        }
        return bookings;
    }

    public List<Booking> findByStatus(Booking.BookingStatus status) {
        List<Booking> bookings = bookingRepository.findByStatus(status);
        if (bookings.isEmpty()) {
            throw new ResourceNotFoundException("No bookings found with status: " + status);
        }
        return bookings;
    }

    @Transactional
    public Booking save(Booking booking) {
        //рахує суму
        if (booking.getTickets() != null && !booking.getTickets().isEmpty()) {
            BigDecimal totalAmount = booking.getTickets().stream()
                    .map(Ticket::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            booking.setTotalAmount(totalAmount);
        }
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking createBookingFromDto(BookingCreateDTO bookingCreateDTO) {
        //є юзер?
        try {
            ResponseEntity<UserDTO> userResponse = restTemplate.getForEntity(
                    userServiceUrl + "/api/users/" + bookingCreateDTO.getUserId(), UserDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("User not found with id: " + bookingCreateDTO.getUserId());
        }

        //пошук квитки
        List<Ticket> tickets = new ArrayList<>();
        for (Long ticketId : bookingCreateDTO.getTicketIds()) {
            Ticket ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

            //квиток доступний?
            if (ticket.getStatus() != Ticket.TicketStatus.AVAILABLE) {
                throw new InvalidStateException("Ticket with id: " + ticketId + " is not available");
            }

            tickets.add(ticket);
        }

        Booking booking = new Booking();
        booking.setUserId(bookingCreateDTO.getUserId());
        booking.setBookingDateTime(LocalDateTime.now());
        booking.setStatus(Booking.BookingStatus.PENDING);

        //сума
        BigDecimal totalAmount = tickets.stream()
                .map(Ticket::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        booking.setTotalAmount(totalAmount);

        Booking savedBooking = bookingRepository.save(booking);

        //оновити квитки
        for (Ticket ticket : tickets) {
            ticket.setBooking(savedBooking);
            ticket.setStatus(Ticket.TicketStatus.BOOKED);
            ticketRepository.save(ticket);

            //оновити доступні місця(у bus-service)
            updateTripAvailableSeats(ticket.getTripId(), -1);
        }

        savedBooking.setTickets(tickets);

        return savedBooking;
    }
    @Transactional
    public Booking updateBookingStatus(Long id, Booking.BookingStatus status) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setStatus(status);

            Ticket.TicketStatus ticketStatus;
            switch (status) {
                case CONFIRMED:
                case PAID:
                    ticketStatus = Ticket.TicketStatus.PAID;
                    break;
                case CANCELED:
                    ticketStatus = Ticket.TicketStatus.CANCELED;
                    for (Ticket ticket : booking.getTickets()) {
                        if (ticket.getStatus() != Ticket.TicketStatus.CANCELED) {
                            updateTripAvailableSeats(ticket.getTripId(), 1);
                        }
                    }
                    break;
                default:
                    ticketStatus = Ticket.TicketStatus.BOOKED;
            }

            for (Ticket ticket : booking.getTickets()) {
                ticket.setStatus(ticketStatus);
                ticketRepository.save(ticket);
            }

            return bookingRepository.save(booking);
        }
        throw new ResourceNotFoundException("Booking not found with id: " + id);
    }

    @Transactional
    public void deleteById(Long id) {
        Booking booking = findById(id);

        for (Ticket ticket : booking.getTickets()) {
            if (ticket.getStatus() != Ticket.TicketStatus.CANCELED &&
                    ticket.getStatus() != Ticket.TicketStatus.AVAILABLE) {
                updateTripAvailableSeats(ticket.getTripId(), 1);
            }
        }

        bookingRepository.deleteById(id);
    }

    private void updateTripAvailableSeats(Long tripId, int delta) {
        try {
            restTemplate.put(
                    busServiceUrl + "/api/trips/" + tripId + "/seats?delta=" + delta,
                    null
            );
        } catch (RestClientException e) {
        }
    }
}