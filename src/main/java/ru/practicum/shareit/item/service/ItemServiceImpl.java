package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(ItemCreateDto itemCreateDto, Long ownerId) {
        Stream.<Map.Entry<String, Object>>of(
                        Map.entry("name", itemCreateDto.getName()),
                        Map.entry("description", itemCreateDto.getDescription()),
                        Map.entry("available", itemCreateDto.getAvailable())
                )
                .filter(entry -> {
                    Object value = entry.getValue();
                    if (value == null) return true;
                    if (value instanceof String) return ((String) value).isBlank();
                    return false;
                })
                .findFirst()
                .ifPresent(entry -> {
                    throw new IllegalArgumentException("Item " + entry.getKey() + " is required");
                });

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + ownerId));

        Item item = itemMapper.toItemFromCreateDto(itemCreateDto, owner);
        Item savedItem = itemRepository.save(item);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemUpdateDto itemUpdateDto, Long ownerId) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("User is not the owner of the item");
        }

        itemMapper.updateItemFromUpdateDto(itemUpdateDto, existingItem);

        Item updatedItem = itemRepository.save(existingItem);
        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemRepository.searchAvailableItems(text).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }
}