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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(ItemControllerHeaderConstants.X_SHARER_USER_ID) Long ownerId,
                          @Valid @RequestBody NewItemRequest item) {
        return itemService.createItem(ownerId, item);
    }

    @GetMapping(ItemControllerHeaderConstants.PATH)
    public ExtendedItemDto findItem(@Valid @PathVariable(ItemControllerHeaderConstants.ID) Long itemId,
                                    @RequestHeader(ItemControllerHeaderConstants.X_SHARER_USER_ID) Long ownerId) {
        return itemService.getItemById(itemId, ownerId);
    }

    @GetMapping(ItemControllerHeaderConstants.SEARCH_PATH)
    public Collection<ItemDto> findItemsForTenant(@RequestHeader(value = ItemControllerHeaderConstants.X_SHARER_USER_ID, required = false) Long ownerId,
                                                  @RequestParam(name = "text", defaultValue = "") String text) {
        return itemService.getItemByText(text);
    }

    @GetMapping
    public Collection<ExtendedItemDto> getItems(@RequestHeader(ItemControllerHeaderConstants.X_SHARER_USER_ID) Long ownerId) {
        return itemService.getAllItemsById(ownerId);
    }

    @PatchMapping(ItemControllerHeaderConstants.PATH)
    public ItemDto update(@PathVariable(ItemControllerHeaderConstants.ID) Long itemId,
                          @Valid @RequestBody UpdateItemRequest newItem,
                          @RequestHeader(ItemControllerHeaderConstants.X_SHARER_USER_ID) Long ownerId) {
        return itemService.updateItem(itemId, newItem, ownerId);
    }

    @DeleteMapping(ItemControllerHeaderConstants.PATH)
    public void delete(@Valid @PathVariable(ItemControllerHeaderConstants.ID) Long itemId) {
        itemService.deleteItem(itemId);
    }

    @PostMapping(ItemControllerHeaderConstants.PATH_PLUS_COMMENT_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@RequestHeader(ItemControllerHeaderConstants.X_SHARER_USER_ID) Long authorId,
                                 @PathVariable(ItemControllerHeaderConstants.ID) Long itemId,
                                 @Valid @RequestBody NewCommentRequest newCommentRequest) {
        return itemService.addComment(authorId, itemId, newCommentRequest);
    }
}
