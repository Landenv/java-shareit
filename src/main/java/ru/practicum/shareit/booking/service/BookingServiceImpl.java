package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingCreateDto bookingCreateDto, Long bookerId) {
        User booker = getUserById(bookerId);
        Item item = getItemById(bookingCreateDto.getItemId());

        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available for booking");
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Owner cannot book own item");
        }

        if (bookingCreateDto.getEnd().isBefore(bookingCreateDto.getStart()) ||
                bookingCreateDto.getEnd().equals(bookingCreateDto.getStart())) {
            throw new ValidationException("Invalid booking dates");
        }

        Booking booking = bookingMapper.toBooking(bookingCreateDto, item, booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toBookingResponseDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = getBookingById(bookingId);

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Only owner can approve booking");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking status cannot be changed");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);

        return bookingMapper.toBookingResponseDto(updatedBooking);
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId);

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Booking access denied");
        }

        return bookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId, String state, int from, int size) {
        getUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        LocalDateTime now = LocalDateTime.now();

        switch (state.toUpperCase()) {
            case "ALL":
                return bookingMapper.toBookingResponseDtoList(
                        bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable));
            case "CURRENT":
                return bookingMapper.toBookingResponseDtoList(
                        bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                userId, now, now, pageable));
            case "PAST":
                return bookingMapper.toBookingResponseDtoList(
                        bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now, pageable));
            case "FUTURE":
                return bookingMapper.toBookingResponseDtoList(
                        bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now, pageable));
            case "WAITING":
                return bookingMapper.toBookingResponseDtoList(
                        bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                                userId, BookingStatus.WAITING, pageable));
            case "REJECTED":
                return bookingMapper.toBookingResponseDtoList(
                        bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                                userId, BookingStatus.REJECTED, pageable));
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long ownerId, String state, int from, int size) {
        getUserById(ownerId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        LocalDateTime now = LocalDateTime.now();

        switch (state.toUpperCase()) {
            case "ALL":
                return bookingMapper.toBookingResponseDtoList(
                        bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId, pageable));
            case "CURRENT":
                return bookingMapper.toBookingResponseDtoList(
                        bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                ownerId, now, now, pageable));
            case "PAST":
                return bookingMapper.toBookingResponseDtoList(
                        bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now, pageable));
            case "FUTURE":
                return bookingMapper.toBookingResponseDtoList(
                        bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now, pageable));
            case "WAITING":
                return bookingMapper.toBookingResponseDtoList(
                        bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                                ownerId, BookingStatus.WAITING, pageable));
            case "REJECTED":
                return bookingMapper.toBookingResponseDtoList(
                        bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                                ownerId, BookingStatus.REJECTED, pageable));
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));
    }
}