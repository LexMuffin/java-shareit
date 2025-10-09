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

    public final String path = "/{id}";
    public final String xSharerUserId = "X-Sharer-User-Id";
    public final String searchPath = "/search";
    public final String id = "id";
    public final String commentPath = "/comment";
    public final String pathPlusCommentPath = path + commentPath;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader(xSharerUserId) Long ownerId,
                                         @Valid @RequestBody NewItemRequest item) {
        return itemClient.createItem(ownerId, item);
    }

    @GetMapping(path)
    public ResponseEntity<Object> findItem(@Valid @PathVariable(id) Long itemId,
                                    @RequestHeader(xSharerUserId) Long ownerId) {
        return itemClient.getItemById(itemId, ownerId);
    }

    @GetMapping(searchPath)
    public ResponseEntity<Object> findItemsForTenant(@RequestHeader(value = xSharerUserId, required = false) Long ownerId,
                                                  @RequestParam(name = "text", defaultValue = "") String text) {
        return itemClient.getItemByText(ownerId, text);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(xSharerUserId) Long ownerId) {
        return itemClient.getAllItemsById(ownerId);
    }

    @PatchMapping(path)
    public ResponseEntity<Object> update(@PathVariable(id) Long itemId,
                          @Valid @RequestBody UpdateItemRequest newItem,
                          @RequestHeader(xSharerUserId) Long ownerId) {
        return itemClient.updateItem(itemId, newItem, ownerId);
    }

    @DeleteMapping(path)
    public ResponseEntity<Object> delete(@Valid @PathVariable(id) Long itemId) {
        return itemClient.deleteItem(itemId);
    }

    @PostMapping(pathPlusCommentPath)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addComment(@RequestHeader(xSharerUserId) Long authorId,
                                 @PathVariable(id) Long itemId,
                                 @Valid @RequestBody NewCommentRequest newCommentRequest) {
        return itemClient.addComment(authorId, itemId, newCommentRequest);
    }
}
