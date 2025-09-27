package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        Long ownerId = item.getOwner() != null ? item.getOwner().getId() : null;
        return new ItemDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), ownerId, item.getRequestId());
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        if (itemDto == null) {
            return null;
        }
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(),
                itemDto.getAvailable(), owner, itemDto.getRequestId());
    }
}