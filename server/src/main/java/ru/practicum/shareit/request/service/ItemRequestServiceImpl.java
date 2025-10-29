package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestResponseDto createRequest(ItemRequestCreateDto itemRequestCreateDto, Long userId) {
        User requester = getUserById(userId);
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestCreateDto, requester);
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        return enrichRequestWithItems(savedRequest);
    }

    @Override
    public List<ItemRequestResponseDto> getUserRequests(Long userId) {
        getUserById(userId);
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(this::enrichRequestWithItems)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestResponseDto> getOtherUsersRequests(Long userId, int from, int size) {
        getUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId, pageable);
        return requests.stream()
                .map(this::enrichRequestWithItems)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponseDto getRequestById(Long requestId, Long userId) {
        getUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item request not found with id: " + requestId));
        return enrichRequestWithItems(itemRequest);
    }

    private ItemRequestResponseDto enrichRequestWithItems(ItemRequest itemRequest) {
        ItemRequestResponseDto responseDto = itemRequestMapper.toItemRequestResponseDto(itemRequest);

        List<ItemDto> items = itemRepository.findByRequestId(itemRequest.getId()).stream()
                .map(item -> {
                    ItemDto itemDto = itemMapper.toItemDto(item);
                    // Очищаем поля, которые не нужны в ответе на запрос
                    itemDto.setLastBooking(null);
                    itemDto.setNextBooking(null);
                    itemDto.setComments(List.of());
                    return itemDto;
                })
                .collect(Collectors.toList());

        responseDto.setItems(items);
        return responseDto;
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }
}