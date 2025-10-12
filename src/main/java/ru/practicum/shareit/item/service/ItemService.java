package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemCreateDto itemCreateDto, Long ownerId);

    ItemDto updateItem(Long itemId, ItemUpdateDto itemUpdateDto, Long ownerId);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getItemsByOwner(Long ownerId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(Long itemId, CommentCreateDto commentCreateDto, Long authorId);
}