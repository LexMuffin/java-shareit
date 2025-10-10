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

    private static final String PATH = "/{id}";
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private static final String SEARCH_PATH = "/search";
    private static final String ID = "id";
    private static final String COMMENT_PATH = "/comment";
    private static final String PATH_PLUS_COMMENT_PATH = PATH + COMMENT_PATH;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                          @Valid @RequestBody NewItemRequest item) {
        return itemService.createItem(ownerId, item);
    }

    @GetMapping(PATH)
    public ExtendedItemDto findItem(@Valid @PathVariable(ID) Long itemId,
                                    @RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        return itemService.getItemById(itemId, ownerId);
    }

    @GetMapping(SEARCH_PATH)
    public Collection<ItemDto> findItemsForTenant(@RequestHeader(value = X_SHARER_USER_ID, required = false) Long ownerId,
                                                  @RequestParam(name = "text", defaultValue = "") String text) {
        return itemService.getItemByText(text);
    }

    @GetMapping
    public Collection<ExtendedItemDto> getItems(@RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        return itemService.getAllItemsById(ownerId);
    }

    @PatchMapping(PATH)
    public ItemDto update(@PathVariable(ID) Long itemId,
                          @Valid @RequestBody UpdateItemRequest newItem,
                          @RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        return itemService.updateItem(itemId, newItem, ownerId);
    }

    @DeleteMapping(PATH)
    public void delete(@Valid @PathVariable(ID) Long itemId) {
        itemService.deleteItem(itemId);
    }

    @PostMapping(PATH_PLUS_COMMENT_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@RequestHeader(X_SHARER_USER_ID) Long authorId,
                                 @PathVariable(ID) Long itemId,
                                 @Valid @RequestBody NewCommentRequest newCommentRequest) {
        return itemService.addComment(authorId, itemId, newCommentRequest);
    }
}
