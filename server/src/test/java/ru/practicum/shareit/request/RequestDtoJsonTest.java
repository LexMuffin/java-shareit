package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class RequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> itemRequestDtoJson;

    private final UserDto userDto = new UserDto(1L, "user", "user@email.com");

    @Test
    public void testItemRequestDtoSerialization() throws IOException {
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        ItemRequestDto requestDto = new ItemRequestDto(1L, "description", 1L, created, List.of());

        JsonContent<ItemRequestDto> content = itemRequestDtoJson.write(requestDto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(content).extractingJsonPathStringValue("$.created").isEqualTo("2024-01-01T10:00:00");
        assertThat(content).hasJsonPathArrayValue("$.items");
        assertThat(content).extractingJsonPathArrayValue("$.items").isEmpty();

        assertThat(content).extractingJsonPathNumberValue("$.requestorId").isEqualTo(1);
    }

    @Test
    public void testItemRequestDtoDeserialization() throws IOException {
        String json = """
            {
                "id": 1,
                "description": "Нужна дрель",
                "requestorId": 1,
                "created": "2024-01-01T10:00:00",
                "items": []
            }
            """;

        ItemRequestDto requestDto = itemRequestDtoJson.parseObject(json);

        // Then
        assertThat(requestDto).isNotNull();
        assertThat(requestDto.getId()).isEqualTo(1);
        assertThat(requestDto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(requestDto.getRequestorId()).isEqualTo(1);
        assertThat(requestDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        assertThat(requestDto.getItems()).isEmpty();
    }
}
