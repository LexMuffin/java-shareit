package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ExtendedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(source = "owner.id", target = "owner")
    @Mapping(source = "requestId", target = "requestId")
    ItemDto mapToItemDto(Item item);

    @Mapping(target = "id", source = "newItemRequest.id")
    @Mapping(target = "name", source = "newItemRequest.name")
    @Mapping(target = "description", source = "newItemRequest.description")
    @Mapping(target = "available", source = "newItemRequest.available")
    @Mapping(target = "requestId", source = "newItemRequest.requestId")
    @Mapping(target = "owner", source = "user")
    Item mapToItem(User user, NewItemRequest newItemRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "requestId", ignore = true)
    void updateItemFromRequest(UpdateItemRequest updateItemRequest, @MappingTarget Item item);

    @Mapping(source = "item.id", target = "id")
    @Mapping(source = "item.name", target = "name")
    @Mapping(source = "item.description", target = "description")
    @Mapping(source = "item.available", target = "available")
    @Mapping(source = "item.owner.id", target = "owner")
    @Mapping(source = "item.requestId", target = "requestId")
    @Mapping(source = "lastBooking", target = "lastBooking")
    @Mapping(source = "nextBooking", target = "nextBooking")
    @Mapping(source = "comments", target = "comments")
    ExtendedItemDto mapToExtendedItemDtoInternal(Item item,
                                                 LocalDateTime lastBooking,
                                                 LocalDateTime nextBooking,
                                                 List<Comment> comments);

    @Mapping(source = "item.id", target = "id")
    @Mapping(source = "item.name", target = "name")
    @Mapping(source = "item.description", target = "description")
    @Mapping(source = "item.available", target = "available")
    @Mapping(source = "item.owner.id", target = "owner")
    @Mapping(source = "item.requestId", target = "requestId")
    @Mapping(source = "comments", target = "comments")
    ExtendedItemDto mapToExtendedItemDto(Item item, List<Comment> comments);

    List<CommentDto> commentsToCommentDtos(List<Comment> comments);

    default ExtendedItemDto mapToExtendedItemDto(Item item,
                                                 Optional<LocalDateTime> bookingLast,
                                                 Optional<LocalDateTime> bookingNext,
                                                 List<Comment> comments) {
        return mapToExtendedItemDtoInternal(
                item,
                bookingLast.orElse(null),
                bookingNext.orElse(null),
                comments
        );
    }


    default Item updateItemFields(Item item, UpdateItemRequest updateItemRequest) {
        if (updateItemRequest.getName() != null) {
            item.setName(updateItemRequest.getName());
        }
        if (updateItemRequest.getDescription() != null) {
            item.setDescription(updateItemRequest.getDescription());
        }
        if (updateItemRequest.getAvailable() != null) {
            item.setAvailable(updateItemRequest.getAvailable());
        }
        return item;
    }
}