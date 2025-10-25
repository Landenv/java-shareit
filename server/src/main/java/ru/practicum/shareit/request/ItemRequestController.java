package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.Headers;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestResponseDto createRequest(@RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                                @RequestHeader(Headers.USER_ID_HEADER) Long userId) {
        return itemRequestService.createRequest(itemRequestCreateDto, userId);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getUserRequests(@RequestHeader(Headers.USER_ID_HEADER) Long userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getOtherUsersRequests(@RequestHeader(Headers.USER_ID_HEADER) Long userId,
                                                              @RequestParam(defaultValue = "0") int from,
                                                              @RequestParam(defaultValue = "10") int size) {
        return itemRequestService.getOtherUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequestById(@PathVariable Long requestId,
                                                 @RequestHeader(Headers.USER_ID_HEADER) Long userId) {
        return itemRequestService.getRequestById(requestId, userId);
    }
}