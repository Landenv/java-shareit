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

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    ItemDto toItemDto(Item item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "itemCreateDto.name")
    @Mapping(target = "description", source = "itemCreateDto.description")
    @Mapping(target = "available", source = "itemCreateDto.available")
    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "requestId", source = "itemCreateDto.requestId")
    Item toItemFromCreateDto(ItemCreateDto itemCreateDto, ru.practicum.shareit.user.model.User owner);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "requestId", ignore = true)
    @Mapping(target = "name", source = "itemUpdateDto.name")
    @Mapping(target = "description", source = "itemUpdateDto.description")
    @Mapping(target = "available", source = "itemUpdateDto.available")
    void updateItemFromUpdateDto(ItemUpdateDto itemUpdateDto, @MappingTarget Item item);
}