package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WaitingBookingStateStrategy implements BookingStateStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findUserBookings(Long userId, Pageable pageable) {
        return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageable);
    }

    @Override
    public List<Booking> findOwnerBookings(Long ownerId, Pageable pageable) {
        return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING, pageable);
    }

    @Override
    public boolean supports(String state) {
        return "WAITING".equalsIgnoreCase(state);
    }
}