package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface BookingStateStrategy {
    List<Booking> findUserBookings(Long userId, Pageable pageable);
    List<Booking> findOwnerBookings(Long ownerId, Pageable pageable);
    boolean supports(String state);
}