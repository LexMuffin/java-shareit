package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.UpdateRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    public final ItemRequestRepository itemRequestRepository;
    public final UserRepository userRepository;
    public final ItemRepository itemRepository;

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id %d не найден", userId)));
    }

    private ItemRequest findItemRequestById(Long itemRequestId) {
        return itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id %d не найден", itemRequestId)));
    }

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(Long userId, NewRequest request) {
        log.info("POST /requests - добавление нового запроса");

        User user = findUserById(userId);
        ItemRequest itemRequest = ItemRequestMapper.INSTANCE.mapToItemRequest(request, user, LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.INSTANCE.mapToItemRequestDto(itemRequest);
    }

    @Override
    @Transactional
    public ItemRequestDto findItemRequest(Long itemRequestId) {
        log.info("GET /requests/{request-id} - получение записи запроса");

        ItemRequest itemRequest = findItemRequestById(itemRequestId);
        List<Item> items = itemRepository.findAllByRequestId(itemRequestId);
        log.info(String.format("Запросы %s", items.toString()));

        return ItemRequestMapper.INSTANCE.mapToItemRequestDto(itemRequest, items);
    }

    private List<ItemRequestDto> getRequestsData(List<ItemRequest> requests) {
        List<Long> requestsIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        Map<Long, List<Item>> requestItems = itemRepository.findAllByRequestIdIn(requestsIds)
                .stream()
                .collect(Collectors.groupingBy(Item::getRequestId, Collectors.toList()));


        List<ItemRequestDto> requestsDto = new ArrayList<>();

        for (ItemRequest request: requests) {
            requestsDto.add(ItemRequestMapper.INSTANCE.mapToItemRequestDto(
                    request,
                    requestItems.getOrDefault(request.getId(), Collections.emptyList())
            ));
        }

        return requestsDto;
    }

    @Override
    @Transactional
    public Collection<ItemRequestDto> findAllByRequestorId(Long requestorId) {
        log.info("GET /requests - получение записей запроса пользователя");
        User user = findUserById(requestorId);

        List<ItemRequest> requests = itemRequestRepository.findByRequestorId(requestorId);

        return getRequestsData(requests)
                .stream()
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .toList();
    }

    @Override
    @Transactional
    public Collection<ItemRequestDto> findAllOfAnotherRequestors(Long requestorId) {
        log.info("GET /requests/all - получение записей запросов, созданных другими пользователями");
        return itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(requestorId)
                .stream()
                .map(ItemRequestMapper.INSTANCE::mapToItemRequestDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .toList();
    }

    @Override
    @Transactional
    public ItemRequestDto update(Long userId, UpdateRequest request) {
        log.info("PATCH /requests - обновление запроса");

        ItemRequest itemRequest = findItemRequestById(request.getId());

        ItemRequest updatedItemRequest = ItemRequestMapper.INSTANCE.updateRequestFromRequest(request, itemRequest);
        return ItemRequestMapper.INSTANCE.mapToItemRequestDto(updatedItemRequest);
    }

    @Override
    @Transactional
    public void delete(Long itemRequestId) {
        log.info("DELETE /requests - удаление запроса");
        ItemRequest itemRequest = findItemRequestById(itemRequestId);
        itemRequestRepository.delete(itemRequest);
    }
}
