package ru.practicum.shareit.item.service;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.enums.Statuses;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotItemOwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.storage.CommentRepository;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtendedItemServiceImpl implements ExtendedItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id %d не найден", userId)));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь c id %d не найдена", itemId)));
    }

    Optional<LocalDateTime> findLastBookingEndByItemId(Long itemId, LocalDateTime dateTime) {
        return bookingRepository.findPastBookingEndByItemId(itemId, Statuses.APPROVED, dateTime)
                .stream()
                .max(Comparator.naturalOrder());
    }

    Optional<LocalDateTime> findNextBookingEndByItemId(Long itemId, LocalDateTime dateTime) {
        return bookingRepository.findFutureBookingEndByItemId(itemId, Statuses.APPROVED, dateTime)
                .stream()
                .min(Comparator.naturalOrder());
    }

    @Override
    @Transactional
    public ItemDto createItem(Long ownerId, NewItemRequest request) {
        log.info("POST /items - добавление новой вещи");

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id %d не найден", ownerId)));

        Item item = ItemMapper.INSTANCE.mapToItem(owner, request);
        itemRepository.save(item);

        return ItemMapper.INSTANCE.mapToItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, UpdateItemRequest request, Long ownerId) {
        log.info("PATCH /items - обновление существующей вещи");

        Item item = findItemById(itemId);

        userRepository.findById(ownerId);

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new NotItemOwnerException("Редактировать данные вещи может только её владелец");
        }

        Item updatedItem = itemRepository.save(ItemMapper.INSTANCE.updateItemFields(item, request));
        return ItemMapper.INSTANCE.mapToItemDto(updatedItem);
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        log.info("DELETE /users - удаление существующего пользователя");
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public ExtendedItemDto getItemById(Long itemId, Long ownerId) {
        log.info("GET /items/{id} - получение существующей вещи");

        Item item = findItemById(itemId);
        if (item.getOwner().getId().equals(ownerId)) {
            return ItemMapper.INSTANCE.mapToExtendedItemDto(
                    item,
                    findLastBookingEndByItemId(itemId, LocalDateTime.now()),
                    findNextBookingEndByItemId(itemId, LocalDateTime.now()),
                    commentRepository.findAllByItemId(itemId)
            );
        }

        return ItemMapper.INSTANCE.mapToExtendedItemDto(item, commentRepository.findAllByItemId(itemId));
    }

    @Override
    public Collection<ItemDto> getItemByText(String text) {
        log.info("GET /items/search - получение вещей по тексту");
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }
        return itemRepository.findByText(text).stream()
                .map(ItemMapper.INSTANCE::mapToItemDto)
                .toList();
    }

    private List<ExtendedItemDto> getItemsData(List<Item> items) {
        List<Long> itemIds = items.stream().map(Item::getId).toList();

        Map<Item, LocalDateTime> pastBookings = bookingRepository
                .findPastBookingEndDatesForItems(itemIds, Statuses.APPROVED, LocalDateTime.now())
                .stream()
                .collect(Collectors.toMap(Booking::getItem, Booking::getEnd));

        Map<Item, LocalDateTime> futureBookings = bookingRepository
                .findFutureBookingStartDatesForItems(itemIds, Statuses.APPROVED, LocalDateTime.now())
                .stream()
                .collect(Collectors.toMap(Booking::getItem, Booking::getStart));

        Map<Item, List<Comment>> comments = commentRepository.findCommentsForItems(itemIds)
                .stream()
                .collect(Collectors.groupingBy(Comment::getItem, Collectors.toList()));

        List<ExtendedItemDto> itemsData = new ArrayList<>();
        for (Item item: items) {
            Optional<LocalDateTime> lastEndDate;
            Optional<LocalDateTime> nextStartDate;
            if (!pastBookings.isEmpty()) {
                lastEndDate = Optional.of(pastBookings.get(item));
            } else {
                lastEndDate = Optional.empty();
            }

            if (!futureBookings.isEmpty()) {
                nextStartDate = Optional.of(futureBookings.get(item));
            } else {
                nextStartDate = Optional.empty();
            }

            itemsData.add(ItemMapper.INSTANCE.mapToExtendedItemDto(
                    item,
                    lastEndDate,
                    nextStartDate,
                    comments.getOrDefault(item, List.of())
            ));
        }

        return itemsData;

    }

    @Override
    @Transactional
    public Collection<ExtendedItemDto> getAllItemsById(Long ownerId) {
        log.info("GET /items/{id} - получение вещей по пользователю");
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        log.info("Вещи получены");
        if (!items.isEmpty()) {
            return getItemsData(items);
        }

        return List.of();
    }

    @Override
    @Transactional
    public CommentDto addComment(Long authorId, Long itemId, NewCommentRequest newCommentRequest) {
        log.info("Добавление комментария к вещи");

        User author = findUserById(authorId);
        Item item = findItemById(itemId);

        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(authorId, itemId, LocalDateTime.now())) {
            throw new ValidationException(String.format("Пользователь %d не может оставить комментарий," +
                    " если не пользовался вещью", authorId));
        }

        Comment comment = CommentMapper.INSTANCE.mapToComment(author, item, newCommentRequest);
        commentRepository.save(comment);

        return CommentMapper.INSTANCE.mapToCommentDto(comment);

    }
}
