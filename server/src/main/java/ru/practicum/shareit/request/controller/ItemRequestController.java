package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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

    private static final String PATH = "/{request-ID}";
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private static final String ID = "request-ID";
    private static final String ALL_PATH = "/all";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                            @RequestBody NewRequest request) {
        return itemRequestService.createItemRequest(userId, request);
    }

    @GetMapping(PATH)
    public ItemRequestDto findItemRequest(@PathVariable(ID) Long requestId,
                                          @RequestHeader(X_SHARER_USER_ID) Long userId) {
        return itemRequestService.findItemRequest(requestId);
    }

    @GetMapping
    public Collection<ItemRequestDto> findAllByRequestorId(
            @RequestHeader(X_SHARER_USER_ID) Long requestorId) {
        return itemRequestService.findAllByRequestorId(requestorId);
    }

    @GetMapping(ALL_PATH)
    public Collection<ItemRequestDto> findAllOfAnotherRequestors(
            @RequestHeader(X_SHARER_USER_ID) Long requestorId) {
        return itemRequestService.findAllOfAnotherRequestors(requestorId);
    }

    @PatchMapping
    public ItemRequestDto update(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestBody UpdateRequest request) {
        return itemRequestService.update(userId, request);
    }

    @DeleteMapping(PATH)
    public void delete(@PathVariable(ID) Long itemRequestId) {
        itemRequestService.delete(itemRequestId);
    }
}
