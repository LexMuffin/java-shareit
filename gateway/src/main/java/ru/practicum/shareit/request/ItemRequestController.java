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

    public final String path = "/{request-id}";
    public final String xSharerUserId = "X-Sharer-User-Id";
    public final String id = "request-id";
    public final String allPath = "/all";

    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItemRequest(@RequestHeader(xSharerUserId) Long userId,
                                            @RequestBody NewRequest request) {
        return itemRequestClient.createItemRequest(userId, request);
    }

    @GetMapping(path)
    public ResponseEntity<Object> findItemRequest(@PathVariable(id) Long requestId,
                                          @RequestHeader(xSharerUserId) Long userId) {
        return itemRequestClient.findItemRequest(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByRequestorId(
            @RequestHeader(xSharerUserId) Long requestorId) {
        return itemRequestClient.findAllByRequestorId(requestorId);
    }

    @GetMapping(allPath)
    public ResponseEntity<Object> findAllOfAnotherRequestors(
            @RequestHeader(xSharerUserId) Long requestorId) {
        return itemRequestClient.findAllOfAnotherRequestors(requestorId);
    }

    @PatchMapping
    public ResponseEntity<Object> update(
            @RequestHeader(xSharerUserId) Long userId,
            @RequestBody UpdateRequest request) {
        return itemRequestClient.update(userId, request);
    }

    @DeleteMapping(path)
    public ResponseEntity<Object> delete(@PathVariable(id) Long itemRequestId) {
        return itemRequestClient.delete(itemRequestId);
    }
}
