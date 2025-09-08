package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final String path = "/{id}";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long ownerId,
                          @Valid @RequestBody NewItemRequest item) {
        return itemService.createItem(ownerId, item);
    }

    @GetMapping(path)
    public ItemDto findItem(@Valid @PathVariable("id") Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItemsForTenant(@RequestHeader(value = HeaderConstants.X_SHARER_USER_ID, required = false) Long ownerId,
                                                  @RequestParam(name = "text", defaultValue = "") String text) {
        return itemService.getItemByText(text);
    }

    @GetMapping
    public Collection<ItemDto> getItems(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long ownerId) {
        return itemService.getAllItemsById(ownerId);
    }

    @PatchMapping(path)
    public ItemDto update(@PathVariable("id") Long itemId,
                          @Valid @RequestBody UpdateItemRequest newItem,
                          @RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long ownerId) {
        return itemService.updateItem(itemId, newItem, ownerId);
    }

    @DeleteMapping(path)
    public void delete(@Valid @PathVariable("id") Long itemId) {
        itemService.deleteItem(itemId);
    }
}
