package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> commentDtoJson;

    @Test
    public void testCommentDtoSerialization() throws IOException {
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        CommentDto commentDto = new CommentDto(1L, "comment", 1L, "authorName", created);

        JsonContent<CommentDto> content = commentDtoJson.write(commentDto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.text").isEqualTo("comment");
        assertThat(content).extractingJsonPathStringValue("$.authorName").isEqualTo("authorName");
        assertThat(content).extractingJsonPathStringValue("$.created").isEqualTo("2024-01-01T10:00:00");
    }

    @Test
    public void testCommentDtoDeserialization() throws IOException {
        String json = "{" +
                "\"id\": 1," +
                "\"text\": \"comment\"," +
                "\"authorName\": \"authorName\"," +
                "\"created\": \"2024-01-01T10:00:00\"" +
                "}";

        CommentDto commentDto = commentDtoJson.parseObject(json);

        assertThat(commentDto).isNotNull();
        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getText()).isEqualTo("comment");
        assertThat(commentDto.getAuthorName()).isEqualTo("authorName");
        assertThat(commentDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0, 0));
    }

    @Test
    public void testCommentDtoDeserializationWithPartialData() throws IOException {
        String json = "{" +
                "\"text\": \"comment\"," +
                "\"authorName\": \"authorName\"" +
                "}";

        CommentDto commentDto = commentDtoJson.parseObject(json);

        assertThat(commentDto).isNotNull();
        assertThat(commentDto.getText()).isEqualTo("comment");
        assertThat(commentDto.getAuthorName()).isEqualTo("authorName");
        assertThat(commentDto.getId()).isNull();
        assertThat(commentDto.getCreated()).isNull();
    }
}