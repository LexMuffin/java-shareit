package ru.practicum.shareit.item.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentRequest;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "created", target = "created")
    CommentDto mapToCommentDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item", source = "item")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    Comment mapToComment(User author, Item item, NewCommentRequest newCommentRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    void updateCommentFromCommentDto(CommentDto commentDto, @MappingTarget Comment comment);


}
