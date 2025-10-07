package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.ItemControllerHeaderConstants;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

@Controller("gatewayItemController")
@RequestMapping(path = "/items")
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader(ItemControllerHeaderConstants.X_SHARER_USER_ID) Long ownerId,
                                         @Valid @RequestBody NewItemRequest item) {
        return itemClient.createItem(ownerId, item);
    }

    @GetMapping(ItemControllerHeaderConstants.PATH)
    public ResponseEntity<Object> findItem(@Valid @PathVariable(ItemControllerHeaderConstants.ID) Long itemId,
                                    @RequestHeader(ItemControllerHeaderConstants.X_SHARER_USER_ID) Long ownerId) {
        return itemClient.getItemById(itemId, ownerId);
    }

    @GetMapping(ItemControllerHeaderConstants.SEARCH_PATH)
    public ResponseEntity<Object> findItemsForTenant(@RequestHeader(value = ItemControllerHeaderConstants.X_SHARER_USER_ID, required = false) Long ownerId,
                                                  @RequestParam(name = "text", defaultValue = "") String text) {
        return itemClient.getItemByText(ownerId, text);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(ItemControllerHeaderConstants.X_SHARER_USER_ID) Long ownerId) {
        return itemClient.getAllItemsById(ownerId);
    }

    @PatchMapping(ItemControllerHeaderConstants.PATH)
    public ResponseEntity<Object> update(@PathVariable(ItemControllerHeaderConstants.ID) Long itemId,
                          @Valid @RequestBody UpdateItemRequest newItem,
                          @RequestHeader(ItemControllerHeaderConstants.X_SHARER_USER_ID) Long ownerId) {
        return itemClient.updateItem(itemId, newItem, ownerId);
    }

    @DeleteMapping(ItemControllerHeaderConstants.PATH)
    public ResponseEntity<Object> delete(@Valid @PathVariable(ItemControllerHeaderConstants.ID) Long itemId) {
        return itemClient.deleteItem(itemId);
    }

    @PostMapping(ItemControllerHeaderConstants.PATH_PLUS_COMMENT_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addComment(@RequestHeader(ItemControllerHeaderConstants.X_SHARER_USER_ID) Long authorId,
                                 @PathVariable(ItemControllerHeaderConstants.ID) Long itemId,
                                 @Valid @RequestBody NewCommentRequest newCommentRequest) {
        return itemClient.addComment(authorId, itemId, newCommentRequest);
    }
}
