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

    public final String path = "/{id}";
    public final String xSharerUserId = "X-Sharer-User-Id";
    public final String searchPath = "/search";
    public final String id = "id";
    public final String commentPath = "/comment";
    public final String pathPlusCommentPath = path + commentPath;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(xSharerUserId) Long ownerId,
                          @Valid @RequestBody NewItemRequest item) {
        return itemService.createItem(ownerId, item);
    }

    @GetMapping(path)
    public ExtendedItemDto findItem(@Valid @PathVariable(id) Long itemId,
                                    @RequestHeader(xSharerUserId) Long ownerId) {
        return itemService.getItemById(itemId, ownerId);
    }

    @GetMapping(searchPath)
    public Collection<ItemDto> findItemsForTenant(@RequestHeader(value = xSharerUserId, required = false) Long ownerId,
                                                  @RequestParam(name = "text", defaultValue = "") String text) {
        return itemService.getItemByText(text);
    }

    @GetMapping
    public Collection<ExtendedItemDto> getItems(@RequestHeader(xSharerUserId) Long ownerId) {
        return itemService.getAllItemsById(ownerId);
    }

    @PatchMapping(path)
    public ItemDto update(@PathVariable(id) Long itemId,
                          @Valid @RequestBody UpdateItemRequest newItem,
                          @RequestHeader(xSharerUserId) Long ownerId) {
        return itemService.updateItem(itemId, newItem, ownerId);
    }

    @DeleteMapping(path)
    public void delete(@Valid @PathVariable(id) Long itemId) {
        itemService.deleteItem(itemId);
    }

    @PostMapping(pathPlusCommentPath)
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@RequestHeader(xSharerUserId) Long authorId,
                                 @PathVariable(id) Long itemId,
                                 @Valid @RequestBody NewCommentRequest newCommentRequest) {
        return itemService.addComment(authorId, itemId, newCommentRequest);
    }
}
