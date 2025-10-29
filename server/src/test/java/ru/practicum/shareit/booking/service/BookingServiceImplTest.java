package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private List<BookingStateStrategy> bookingStrategies;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBooking_WithNonExistingItem_ShouldThrowException() {
        Long itemId = 999L;
        Long bookerId = 1L;
        BookingCreateDto createDto = new BookingCreateDto(
                itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)
        );

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(createDto, bookerId));

        verify(userRepository).findById(bookerId);
        verify(itemRepository).findById(itemId);
        verifyNoInteractions(bookingMapper);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void createBooking_WithOwnerBookingOwnItem_ShouldThrowException() {
        Long ownerId = 1L;
        Long itemId = 1L;
        BookingCreateDto createDto = new BookingCreateDto(
                itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)
        );

        User owner = new User(ownerId, "Owner", "owner@mail.com");
        Item item = new Item(itemId, "Drill", "Powerful", true, owner, null);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(createDto, ownerId));

        verify(userRepository).findById(ownerId);
        verify(itemRepository).findById(itemId);
        verifyNoInteractions(bookingMapper);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void createBooking_WithUnavailableItem_ShouldThrowException() {
        Long bookerId = 2L;
        Long itemId = 1L;
        Long ownerId = 1L;
        BookingCreateDto createDto = new BookingCreateDto(
                itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)
        );

        User booker = new User(bookerId, "Booker", "booker@mail.com");
        User owner = new User(ownerId, "Owner", "owner@mail.com");
        Item item = new Item(itemId, "Drill", "Powerful", false, owner, null); // available = false

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(createDto, bookerId));

        verify(userRepository).findById(bookerId);
        verify(itemRepository).findById(itemId);
        verifyNoInteractions(bookingMapper);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void createBooking_WithInvalidDates_ShouldThrowException() {
        Long bookerId = 2L;
        Long itemId = 1L;
        Long ownerId = 1L;
        BookingCreateDto createDto = new BookingCreateDto(
                itemId, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1) // end before start
        );

        User booker = new User(bookerId, "Booker", "booker@mail.com");
        User owner = new User(ownerId, "Owner", "owner@mail.com");
        Item item = new Item(itemId, "Drill", "Powerful", true, owner, null);

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(createDto, bookerId));
    }

    @Test
    void getBookingById_WithNonExistingBooking_ShouldThrowException() {
        Long bookingId = 999L;
        Long userId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(bookingId, userId));

        verify(bookingRepository).findById(bookingId);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getBookingById_WithUnauthorizedUser_ShouldThrowException() {
        Long bookingId = 1L;
        Long userId = 3L; // Не владелец и не бронирующий

        User booker = new User(1L, "Booker", "booker@mail.com");
        User owner = new User(2L, "Owner", "owner@mail.com");
        User otherUser = new User(3L, "Other", "other@mail.com");
        Item item = new Item(1L, "Drill", "Powerful", true, owner, null);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(bookingId, userId));

        verify(bookingRepository).findById(bookingId);
    }
}