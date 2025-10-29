package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_WithDuplicateEmail_ShouldThrowException() {
        UserCreateDto createDto = new UserCreateDto("John", "john@mail.com");

        when(userRepository.existsByEmail("john@mail.com")).thenReturn(true);

        assertThrows(EmailConflictException.class, () -> userService.createUser(createDto));

        verify(userRepository).existsByEmail("john@mail.com");
        verifyNoInteractions(userMapper);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserById_WithNonExistingUser_ShouldThrowException() {
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));

        verify(userRepository).findById(userId);
    }

    @Test
    void updateUser_WithDuplicateEmail_ShouldThrowException() {
        Long userId = 1L;
        User existingUser = new User(userId, "Old Name", "old@mail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmailAndIdNot("new@mail.com", userId))
                .thenReturn(Optional.of(new User(2L, "Other User", "new@mail.com")));

        assertThrows(EmailConflictException.class, () ->
                userService.updateUser(userId, new ru.practicum.shareit.user.dto.UserUpdateDto("New Name", "new@mail.com")));

        verify(userRepository).findById(userId);
        verify(userRepository).findByEmailAndIdNot("new@mail.com", userId);
        verifyNoInteractions(userMapper);
    }

    @Test
    void deleteUser_WithNonExistingUser_ShouldThrowException() {
        Long userId = 999L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));

        verify(userRepository).existsById(userId);
        verifyNoMoreInteractions(userRepository);
    }
}