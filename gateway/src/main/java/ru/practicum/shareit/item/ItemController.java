package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemCreateDto itemCreateDto,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Creating item {}, ownerId={}", itemCreateDto, ownerId);
        return itemClient.createItem(itemCreateDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                                             @Valid @RequestBody ItemUpdateDto itemUpdateDto,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Updating item {}, itemId={}, ownerId={}", itemUpdateDto, itemId, ownerId);
        return itemClient.updateItem(itemId, itemUpdateDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get item, itemId={}, userId={}", itemId, userId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Get items by owner, ownerId={}", ownerId);
        return itemClient.getItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Search items, text={}, from={}, size={}", text, from, size);
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @Valid @RequestBody CommentCreateDto commentCreateDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Adding comment to item, itemId={}, userId={}", itemId, userId);
        return itemClient.addComment(itemId, commentCreateDto, userId);
    }
}