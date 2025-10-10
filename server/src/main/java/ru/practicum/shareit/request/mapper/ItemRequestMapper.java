package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.dto.UpdateRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemRequestMapper {

    ItemRequestMapper INSTANCE = Mappers.getMapper(ItemRequestMapper.class);

    @Mapping(source = "item.id", target = "id")
    @Mapping(source = "item.name", target = "name")
    @Mapping(source = "item.owner.id", target = "ownerId")
    ResponseDto mapToResponseDto(Item item);

    @Mapping(source = "itemRequest.description", target = "description")
    @Mapping(source = "itemRequest.requestor.id", target = "requestorId")
    @Mapping(source = "itemRequest.created", target = "created")
    @Mapping(target = "items", expression = "java(java.util.Collections.emptyList())")
    ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest);

    @Mapping(source = "itemRequest.description", target = "description")
    @Mapping(source = "itemRequest.requestor.id", target = "requestorId")
    @Mapping(source = "itemRequest.created", target = "created")
    @Mapping(target = "items", source = "items")
    ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest, Collection<Item> items);

    @Mapping(source = "request.description", target = "description")
    @Mapping(source = "user", target = "requestor")
    @Mapping(source = "datetime", target = "created")
    ItemRequest mapToItemRequest(NewRequest request, User user, LocalDateTime datetime);

    List<ResponseDto> mapToResponseDtoList(List<Item> items);

    @Mapping(source = "updateRequest.description", target = "itemRequest.description")
    ItemRequest updateRequestFromRequest(UpdateRequest updateRequest, @MappingTarget ItemRequest itemRequest);
}
