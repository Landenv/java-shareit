package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requester", source = "requester")
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "items", ignore = true)
    ItemRequest toItemRequest(ItemRequestCreateDto itemRequestCreateDto, User requester);

    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    @Mapping(target = "items", ignore = true)
    ItemRequestResponseDto toItemRequestResponseDto(ItemRequest itemRequest);
}