package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingCreateDto bookingCreateDto, Long bookerId);
    BookingResponseDto approveBooking(Long bookingId, Boolean approved, Long ownerId);
    BookingResponseDto getBookingById(Long bookingId, Long userId);
    List<BookingResponseDto> getUserBookings(Long userId, String state, int from, int size);
    List<BookingResponseDto> getOwnerBookings(Long ownerId, String state, int from, int size);
}