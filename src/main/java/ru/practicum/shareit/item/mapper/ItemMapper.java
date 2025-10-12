package ru.practicum.shareit.item.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    ItemDto toItemDto(Item item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "itemCreateDto.name")
    Item toItemFromCreateDto(ItemCreateDto itemCreateDto, User owner);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateItemFromUpdateDto(ItemUpdateDto itemUpdateDto, @MappingTarget Item item);
}