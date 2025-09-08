package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto createItem(Long ownerId, NewItemRequest request);

    ItemDto updateItem(Long itemId, UpdateItemRequest request, Long ownerId);

    void deleteItem(Long itemId);

    ItemDto getItemById(Long itemId);

    Collection<ItemDto> getItemByText(String text);

    Collection<ItemDto> getAllItemsById(Long ownerId);

    Collection<ItemDto> getAllItems();
}
