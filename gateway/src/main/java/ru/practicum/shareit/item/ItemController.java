package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

@Controller("gatewayItemController")
@RequestMapping(path = "/items")
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    public final String PATH = "/{id}";
    public final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    public final String SEARCH_PATH = "/search";
    public final String ID = "id";
    public final String COMMENT_PATH = "/comment";
    public final String PATH_PLUS_COMMENT_PATH = PATH + COMMENT_PATH;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                         @Valid @RequestBody NewItemRequest item) {
        return itemClient.createItem(ownerId, item);
    }

    @GetMapping(PATH)
    public ResponseEntity<Object> findItem(@Valid @PathVariable(ID) Long itemId,
                                    @RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        return itemClient.getItemById(itemId, ownerId);
    }

    @GetMapping(SEARCH_PATH)
    public ResponseEntity<Object> findItemsForTenant(@RequestHeader(value = X_SHARER_USER_ID, required = false) Long ownerId,
                                                  @RequestParam(name = "text", defaultValue = "") String text) {
        return itemClient.getItemByText(ownerId, text);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        return itemClient.getAllItemsById(ownerId);
    }

    @PatchMapping(PATH)
    public ResponseEntity<Object> update(@PathVariable(ID) Long itemId,
                          @Valid @RequestBody UpdateItemRequest newItem,
                          @RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        return itemClient.updateItem(itemId, newItem, ownerId);
    }

    @DeleteMapping(PATH)
    public ResponseEntity<Object> delete(@Valid @PathVariable(ID) Long itemId) {
        return itemClient.deleteItem(itemId);
    }

    @PostMapping(PATH_PLUS_COMMENT_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addComment(@RequestHeader(X_SHARER_USER_ID) Long authorId,
                                 @PathVariable(ID) Long itemId,
                                 @Valid @RequestBody NewCommentRequest newCommentRequest) {
        return itemClient.addComment(authorId, itemId, newCommentRequest);
    }
}
