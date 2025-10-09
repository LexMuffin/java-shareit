package ru.practicum.shareit.request;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.UpdateRequest;

@Controller("gatewayItemRequestController")
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    public final ItemRequestClient itemRequestClient;

    public final String PATH = "/{request-id}";
    public final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    public final String ID = "request-id";
    public final String ALL_PATH = "/all";

    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItemRequest(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                            @RequestBody NewRequest request) {
        return itemRequestClient.createItemRequest(userId, request);
    }

    @GetMapping(PATH)
    public ResponseEntity<Object> findItemRequest(@PathVariable(ID) Long requestId,
                                          @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemRequestClient.findItemRequest(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByRequestorId(
            @RequestHeader(X_SHARER_USER_ID) Long requestorId) {
        return itemRequestClient.findAllByRequestorId(requestorId);
    }

    @GetMapping(ALL_PATH)
    public ResponseEntity<Object> findAllOfAnotherRequestors(
            @RequestHeader(X_SHARER_USER_ID) Long requestorId) {
        return itemRequestClient.findAllOfAnotherRequestors(requestorId);
    }

    @PatchMapping
    public ResponseEntity<Object> update(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestBody UpdateRequest request) {
        return itemRequestClient.update(userId, request);
    }

    @DeleteMapping(PATH)
    public ResponseEntity<Object> delete(@PathVariable(ID) Long itemRequestId) {
        return itemRequestClient.delete(itemRequestId);
    }
}
