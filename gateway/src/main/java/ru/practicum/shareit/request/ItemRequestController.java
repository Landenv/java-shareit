package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.shareit.util.Headers;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                                @RequestHeader(Headers.USER_ID_HEADER) Long userId) {
        log.info("Creating item request {}, userId={}", itemRequestCreateDto, userId);
        return itemRequestClient.createRequest(itemRequestCreateDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(Headers.USER_ID_HEADER) Long userId) {
        log.info("Get user requests, userId={}", userId);
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersRequests(@RequestHeader(Headers.USER_ID_HEADER) Long userId,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get other users requests, userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getOtherUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable Long requestId,
                                                 @RequestHeader(Headers.USER_ID_HEADER) Long userId) {
        log.info("Get request by id, requestId={}, userId={}", requestId, userId);
        return itemRequestClient.getRequestById(requestId, userId);
    }
}