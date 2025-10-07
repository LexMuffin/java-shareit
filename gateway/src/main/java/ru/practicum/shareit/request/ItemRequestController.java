package ru.practicum.shareit.request;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.ItemRequestControllerHeaderConstants;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.UpdateRequest;

@Controller("gatewayItemRequestController")
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    public final ItemRequestClient itemRequestClient;

    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItemRequest(@RequestHeader(ItemRequestControllerHeaderConstants.X_SHARER_USER_ID) Long userId,
                                            @RequestBody NewRequest request) {
        return itemRequestClient.createItemRequest(userId, request);
    }

    @GetMapping(ItemRequestControllerHeaderConstants.PATH)
    public ResponseEntity<Object> findItemRequest(@PathVariable(ItemRequestControllerHeaderConstants.ID) Long requestId,
                                          @RequestHeader(ItemRequestControllerHeaderConstants.X_SHARER_USER_ID) Long userId) {
        return itemRequestClient.findItemRequest(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByRequestorId(
            @RequestHeader(ItemRequestControllerHeaderConstants.X_SHARER_USER_ID) Long requestorId) {
        return itemRequestClient.findAllByRequestorId(requestorId);
    }

    @GetMapping(ItemRequestControllerHeaderConstants.ALL_PATH)
    public ResponseEntity<Object> findAllOfAnotherRequestors(
            @RequestHeader(ItemRequestControllerHeaderConstants.X_SHARER_USER_ID) Long requestorId) {
        return itemRequestClient.findAllOfAnotherRequestors(requestorId);
    }

    @PatchMapping
    public ResponseEntity<Object> update(
            @RequestHeader(ItemRequestControllerHeaderConstants.X_SHARER_USER_ID) Long userId,
            @RequestBody UpdateRequest request) {
        return itemRequestClient.update(userId, request);
    }

    @DeleteMapping(ItemRequestControllerHeaderConstants.PATH)
    public ResponseEntity<Object> delete(@PathVariable(ItemRequestControllerHeaderConstants.ID) Long itemRequestId) {
        return itemRequestClient.delete(itemRequestId);
    }
}
