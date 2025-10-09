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

    public final String path = "/{request-id}";
    public final String xSharerUserId = "X-Sharer-User-Id";
    public final String id = "request-id";
    public final String allPath = "/all";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@RequestHeader(xSharerUserId) Long userId,
                                            @RequestBody NewRequest request) {
        return itemRequestService.createItemRequest(userId, request);
    }

    @GetMapping(path)
    public ItemRequestDto findItemRequest(@PathVariable(id) Long requestId,
                                          @RequestHeader(xSharerUserId) Long userId) {
        return itemRequestService.findItemRequest(requestId);
    }

    @GetMapping
    public Collection<ItemRequestDto> findAllByRequestorId(
            @RequestHeader(xSharerUserId) Long requestorId) {
        return itemRequestService.findAllByRequestorId(requestorId);
    }

    @GetMapping(allPath)
    public Collection<ItemRequestDto> findAllOfAnotherRequestors(
            @RequestHeader(xSharerUserId) Long requestorId) {
        return itemRequestService.findAllOfAnotherRequestors(requestorId);
    }

    @PatchMapping
    public ItemRequestDto update(
            @RequestHeader(xSharerUserId) Long userId,
            @RequestBody UpdateRequest request) {
        return itemRequestService.update(userId, request);
    }

    @DeleteMapping(path)
    public void delete(@PathVariable(id) Long itemRequestId) {
        itemRequestService.delete(itemRequestId);
    }
}
