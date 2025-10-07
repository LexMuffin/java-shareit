package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.controller.BookingControllerHeaderConstants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.UpdateRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    public final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@RequestHeader(ItemRequestControllerHeaderConstants.X_SHARER_USER_ID) Long userId,
                                            @RequestBody NewRequest request) {
        return itemRequestService.createItemRequest(userId, request);
    }

    @GetMapping(ItemRequestControllerHeaderConstants.PATH)
    public ItemRequestDto findItemRequest(@PathVariable(ItemRequestControllerHeaderConstants.ID) Long requestId,
                                          @RequestHeader(ItemRequestControllerHeaderConstants.X_SHARER_USER_ID) Long userId) {
        return itemRequestService.findItemRequest(requestId);
    }

    @GetMapping
    public Collection<ItemRequestDto> findAllByRequestorId(
            @RequestHeader(ItemRequestControllerHeaderConstants.X_SHARER_USER_ID) Long requestorId) {
        return itemRequestService.findAllByRequestorId(requestorId);
    }

    @GetMapping(ItemRequestControllerHeaderConstants.ALL_PATH)
    public Collection<ItemRequestDto> findAllOfAnotherRequestors(
            @RequestHeader(ItemRequestControllerHeaderConstants.X_SHARER_USER_ID) Long requestorId) {
        return itemRequestService.findAllOfAnotherRequestors(requestorId);
    }

    @PatchMapping
    public ItemRequestDto update(
            @RequestHeader(ItemRequestControllerHeaderConstants.X_SHARER_USER_ID) Long userId,
            @RequestBody UpdateRequest request) {
        return itemRequestService.update(userId, request);
    }

    @DeleteMapping(ItemRequestControllerHeaderConstants.PATH)
    public void delete(@PathVariable(ItemRequestControllerHeaderConstants.ID) Long itemRequestId) {
        itemRequestService.delete(itemRequestId);
    }
}
