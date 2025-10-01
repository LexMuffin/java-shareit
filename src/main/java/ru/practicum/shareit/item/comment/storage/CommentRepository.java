package ru.practicum.shareit.item.comment.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItemId(Long itemId);

    @Query("select c from Comment c where item.id in (:itemIds) order by created")
    List<Comment> findCommentsForItems(@Param("itemIds") List<Long> itemIds);
}
