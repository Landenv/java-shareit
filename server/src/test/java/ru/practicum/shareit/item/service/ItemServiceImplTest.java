package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void getItemById_WithNonExistingItem_ShouldThrowException() {
        Long itemId = 999L;
        Long userId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(itemId, userId));

        verify(itemRepository).findById(itemId);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void createItem_WithNonExistingUser_ShouldThrowException() {
        Long ownerId = 999L;
        ItemCreateDto createDto = new ItemCreateDto("Drill", "Powerful", true, null);

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(createDto, ownerId));

        verify(userRepository).findById(ownerId);
        verifyNoInteractions(itemMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void updateItem_WithNonOwner_ShouldThrowException() {
        Long itemId = 1L;
        Long ownerId = 2L; // Не владелец
        Long actualOwnerId = 1L;

        User owner = new User(actualOwnerId, "Owner", "owner@mail.com");
        Item existingItem = new Item(itemId, "Drill", "Powerful", true, owner, null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        assertThrows(NotFoundException.class, () ->
                itemService.updateItem(itemId, new ru.practicum.shareit.item.dto.ItemUpdateDto(), ownerId));

        verify(itemRepository).findById(itemId);
        verifyNoMoreInteractions(itemRepository);
    }
}