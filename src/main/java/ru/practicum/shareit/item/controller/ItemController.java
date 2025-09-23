package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ExtendedItemService;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ExtendedItemService itemService;
    private final String path = "/{id}";
    private final String commentPath = "/comment";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long ownerId,
                          @Valid @RequestBody NewItemRequest item) {
        return itemService.createItem(ownerId, item);
    }

    @GetMapping(path)
    public ExtendedItemDto findItem(@Valid @PathVariable("id") Long itemId,
                                    @RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long ownerId) {
        return itemService.getItemById(itemId, ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItemsForTenant(@RequestHeader(value = HeaderConstants.X_SHARER_USER_ID, required = false) Long ownerId,
                                                  @RequestParam(name = "text", defaultValue = "") String text) {
        return itemService.getItemByText(text);
    }

    @GetMapping
    public Collection<ExtendedItemDto> getItems(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long ownerId) {
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

    @PostMapping(path + commentPath)
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long authorId,
                                 @PathVariable("id") Long itemId,
                                 @Valid @RequestBody NewCommentRequest newCommentRequest) {
        return itemService.addComment(authorId, itemId, newCommentRequest);
    }
}
