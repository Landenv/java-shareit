package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FutureBookingStateStrategy implements BookingStateStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findUserBookings(Long userId, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now, pageable);
    }

    @Override
    public List<Booking> findOwnerBookings(Long ownerId, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now, pageable);
    }

    @Override
    public boolean supports(BookingState state) {
        return state == BookingState.FUTURE;
    }
}