package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_WithExistingEmail_ShouldReturnUser() {
        User user = new User(null, "John", "john@mail.com");
        entityManager.persist(user);

        Optional<User> found = userRepository.findByEmail("john@mail.com");

        assertTrue(found.isPresent());
        assertEquals("John", found.get().getName());
    }

    @Test
    void existsByEmail_WithExistingEmail_ShouldReturnTrue() {
        User user = new User(null, "John", "john@mail.com");
        entityManager.persist(user);

        boolean exists = userRepository.existsByEmail("john@mail.com");

        assertTrue(exists);
    }
}