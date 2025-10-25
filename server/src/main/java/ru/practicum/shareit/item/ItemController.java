package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Headers;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestBody ItemCreateDto itemCreateDto,
                              @RequestHeader(Headers.USER_ID_HEADER) Long ownerId) {
        return itemService.createItem(itemCreateDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestBody ItemUpdateDto itemUpdateDto,
                              @RequestHeader(Headers.USER_ID_HEADER) Long ownerId) {
        return itemService.updateItem(itemId, itemUpdateDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId,
                               @RequestHeader(Headers.USER_ID_HEADER) Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(Headers.USER_ID_HEADER) Long ownerId) {
        return itemService.getItemsByOwner(ownerId);
    }

    // ИСПРАВЛЕНИЕ: убираем неиспользуемые параметры from/size
    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestBody CommentCreateDto commentCreateDto,
                                 @RequestHeader(Headers.USER_ID_HEADER) Long userId) {
        return itemService.addComment(itemId, commentCreateDto, userId);
    }
}