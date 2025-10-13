package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AllBookingStateStrategy implements BookingStateStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findUserBookings(Long userId, Pageable pageable) {
        return bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable);
    }

    @Override
    public List<Booking> findOwnerBookings(Long ownerId, Pageable pageable) {
        return bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId, pageable);
    }

    @Override
    public boolean supports(String state) {
        return "ALL".equalsIgnoreCase(state);
    }
}