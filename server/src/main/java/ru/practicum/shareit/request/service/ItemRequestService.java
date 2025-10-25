package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponseDto createRequest(ItemRequestCreateDto itemRequestCreateDto, Long userId);
    List<ItemRequestResponseDto> getUserRequests(Long userId);
    List<ItemRequestResponseDto> getOtherUsersRequests(Long userId, int from, int size);
    ItemRequestResponseDto getRequestById(Long requestId, Long userId);
}