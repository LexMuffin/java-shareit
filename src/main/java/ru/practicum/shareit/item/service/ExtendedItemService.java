package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Collection;

public interface ExtendedItemService {

    ItemDto createItem(Long ownerId, NewItemRequest request);

    ItemDto updateItem(Long itemId, UpdateItemRequest request, Long ownerId);

    void deleteItem(Long itemId);

    ExtendedItemDto getItemById(Long itemId, Long ownerId);

    Collection<ItemDto> getItemByText(String text);

    Collection<ExtendedItemDto> getAllItemsById(Long ownerId);

    CommentDto addComment(Long authorId, Long itemId, NewCommentRequest newCommentRequest);
}
