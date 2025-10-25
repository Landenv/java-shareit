package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    void createItem_WithValidData_ShouldCreateItem() {
        UserCreateDto userCreateDto = new UserCreateDto("John Doe", "john@example.com");
        var userDto = userService.createUser(userCreateDto);

        ItemCreateDto itemCreateDto = new ItemCreateDto("Drill", "Powerful drill", true, null);

        ItemDto result = itemService.createItem(itemCreateDto, userDto.getId());

        assertNotNull(result.getId());
        assertEquals("Drill", result.getName());
        assertEquals("Powerful drill", result.getDescription());
        assertTrue(result.getAvailable());
        assertEquals(userDto.getId(), result.getOwnerId());
    }

    @Test
    void getItemById_WithExistingItem_ShouldReturnItem() {
        UserCreateDto userCreateDto = new UserCreateDto("John Doe", "john@example.com");
        var userDto = userService.createUser(userCreateDto);

        ItemCreateDto itemCreateDto = new ItemCreateDto("Drill", "Powerful drill", true, null);
        var createdItem = itemService.createItem(itemCreateDto, userDto.getId());

        ItemDto result = itemService.getItemById(createdItem.getId(), userDto.getId());

        assertNotNull(result);
        assertEquals(createdItem.getId(), result.getId());
        assertEquals("Drill", result.getName());
    }

    @Test
    void getItemsByOwner_WithExistingItems_ShouldReturnItemsList() {
        UserCreateDto userCreateDto = new UserCreateDto("John Doe", "john@example.com");
        var userDto = userService.createUser(userCreateDto);

        ItemCreateDto item1 = new ItemCreateDto("Drill", "Powerful drill", true, null);
        ItemCreateDto item2 = new ItemCreateDto("Hammer", "Heavy hammer", true, null);

        itemService.createItem(item1, userDto.getId());
        itemService.createItem(item2, userDto.getId());

        var result = itemService.getItemsByOwner(userDto.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(item -> item.getName().equals("Drill")));
        assertTrue(result.stream().anyMatch(item -> item.getName().equals("Hammer")));
    }
}