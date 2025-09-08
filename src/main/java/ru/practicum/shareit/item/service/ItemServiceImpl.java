package ru.practicum.shareit.item.service;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotItemOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto createItem(Long ownerId, NewItemRequest request) {
        log.info("POST /items - добавление новой вещи");

        userStorage.getUserById(ownerId);

        Item item = ItemMapper.mapToItem(ownerId, request);
        itemStorage.createItem(item);

        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long itemId, UpdateItemRequest request, Long ownerId) {
        log.info("PATCH /items - обновление существующей вещи");

        Item item = itemStorage.getItemById(itemId);

        userStorage.getUserById(ownerId);

        if (!item.getOwner().equals(ownerId)) {
            throw new NotItemOwnerException("Редактировать данные вещи может только её владелец");
        }

        Item updatedItem = itemStorage.updateItem(ItemMapper.updateItemFields(item, request));
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public void deleteItem(Long itemId) {
        log.info("DELETE /users - удаление существующего пользователя");
        itemStorage.deleteItem(itemId);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        log.info("GET /items/{id} - получение существующей вещи");
        if (itemId == null || itemStorage.getItemById(itemId) == null) {
            log.error("Вещь с идентификатором - \"{}\" не найдена", itemId);
            throw new NotFoundException("Вещь не найдена");
        }
        return ItemMapper.mapToItemDto(itemStorage.getItemById(itemId));
    }

    @Override
    public Collection<ItemDto> getItemByText(String text) {
        log.info("GET /items/search - получение вещей по тексту");
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }
        return itemStorage.getItemByText(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> getAllItemsById(Long ownerId) {
        log.info("GET /items/{id} - получение вещей по пользователю");
        return itemStorage.getAllItems().stream()
                .filter(item -> item.getOwner().equals(ownerId))
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> getAllItems() {
        log.info("GET /items - получение всех вещей");
        return itemStorage.getAllItems().stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }
}
