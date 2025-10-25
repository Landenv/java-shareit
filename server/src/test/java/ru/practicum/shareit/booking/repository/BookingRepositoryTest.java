package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findByBookerIdOrderByStartDesc_WithExistingBooker_ShouldReturnBookings() {
        User owner = new User(null, "Owner", "owner@mail.com");
        User booker = new User(null, "Booker", "booker@mail.com");
        entityManager.persist(owner);
        entityManager.persist(booker);

        Item item = new Item(null, "Drill", "Powerful", true, owner, null);
        entityManager.persist(item);

        Booking booking = new Booking(null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item, booker, BookingStatus.WAITING);
        entityManager.persist(booking);

        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(
                booker.getId(), PageRequest.of(0, 10));

        assertEquals(1, bookings.size());
        assertEquals("Drill", bookings.get(0).getItem().getName());
    }
}