package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemDto createItem(ItemCreateDto itemCreateDto, Long ownerId) {
        User owner = findUserById(ownerId);
        Item item = itemMapper.toItemFromCreateDto(itemCreateDto, owner);
        Item savedItem = itemRepository.save(item);
        return enrichItemWithBookingsAndComments(savedItem, null, ownerId);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, ItemUpdateDto itemUpdateDto, Long ownerId) {
        Item existingItem = findItemById(itemId);

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("User is not the owner of the item");
        }

        itemMapper.updateItemFromUpdateDto(itemUpdateDto, existingItem);
        Item updatedItem = itemRepository.save(existingItem);

        return enrichItemWithBookingsAndComments(updatedItem, null, ownerId);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = findItemById(itemId);
        return enrichItemWithBookingsAndComments(item, null, userId);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        List<Item> items = itemRepository.findByOwnerIdOrderById(ownerId);

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        Map<Long, List<Comment>> commentsByItemId = commentRepository
                .findByItemIdInOrderByCreatedDesc(itemIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return items.stream()
                .map(item -> enrichItemWithBookingsAndComments(item, commentsByItemId, ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.searchAvailableItems(text).stream()
                .map(item -> enrichItemWithBookingsAndComments(item, null, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, CommentCreateDto commentCreateDto, Long authorId) {
        Item item = findItemById(itemId);
        User author = findUserById(authorId);

        if (item.getOwner().getId().equals(authorId)) {
            throw new ValidationException("Owner cannot comment on own item");
        }

        boolean hasCompletedBooking = bookingRepository
                .findFirstByItemIdAndBookerIdAndEndBeforeAndStatus(
                        itemId, authorId, LocalDateTime.now(), BookingStatus.APPROVED)
                .isPresent();

        if (!hasCompletedBooking) {
            boolean hasAnyApprovedBooking = bookingRepository
                    .findByItemIdAndBookerIdAndStatus(itemId, authorId, BookingStatus.APPROVED)
                    .stream()
                    .findFirst()
                    .isPresent();

            if (hasAnyApprovedBooking) {
                throw new ValidationException("User can only comment on items after the booking has ended");
            } else {
                throw new ValidationException("User can only comment on items they have booked");
            }
        }

        if (commentCreateDto.getText() == null || commentCreateDto.getText().isBlank()) {
            throw new ValidationException("Comment text cannot be empty");
        }

        Comment comment = new Comment();
        comment.setText(commentCreateDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return convertToCommentDto(savedComment);
    }

    private ItemDto enrichItemWithBookingsAndComments(Item item, Map<Long, List<Comment>> commentsByItemId, Long currentUserId) {
        ItemDto itemDto = itemMapper.toItemDto(item);

        boolean isOwner = currentUserId != null && item.getOwner().getId().equals(currentUserId);

        if (isOwner) {
            LocalDateTime now = LocalDateTime.now();
            List<Booking> lastBookings = bookingRepository.findLastBookingForItem(item.getId(), now);
            List<Booking> nextBookings = bookingRepository.findNextBookingForItem(item.getId(), now);

            if (!lastBookings.isEmpty()) {
                Booking lastBooking = lastBookings.get(0);
                itemDto.setLastBooking(new BookingInfo(lastBooking.getId(), lastBooking.getBooker().getId()));
            }

            if (!nextBookings.isEmpty()) {
                Booking nextBooking = nextBookings.get(0);
                itemDto.setNextBooking(new BookingInfo(nextBooking.getId(), nextBooking.getBooker().getId()));
            }
        }

        List<CommentDto> commentDtos;
        if (commentsByItemId != null && commentsByItemId.containsKey(item.getId())) {
            commentDtos = commentsByItemId.get(item.getId()).stream()
                    .map(this::convertToCommentDto)
                    .collect(Collectors.toList());
        } else {
            List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(item.getId());
            commentDtos = comments.stream()
                    .map(this::convertToCommentDto)
                    .collect(Collectors.toList());
        }
        itemDto.setComments(commentDtos);

        return itemDto;
    }

    private CommentDto convertToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));
    }
}