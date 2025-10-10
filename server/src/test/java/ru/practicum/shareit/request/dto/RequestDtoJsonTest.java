package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class RequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> itemRequestDtoJson;

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

        assertThat(requestDto).isNotNull();
        assertThat(requestDto.getId()).isEqualTo(1);
        assertThat(requestDto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(requestDto.getRequestorId()).isEqualTo(1);
        assertThat(requestDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        assertThat(requestDto.getItems()).isEmpty();
    }

    @Test
    public void testItemRequestDtoSerializationWithItems() throws IOException {
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        List<ResponseDto> items = List.of(
                new ResponseDto(1L, "Дрель", 2L),
                new ResponseDto(2L, "Молоток", 3L)
        );
        ItemRequestDto requestDto = new ItemRequestDto(1L, "Нужны инструменты", 1L, created, items);

        JsonContent<ItemRequestDto> content = itemRequestDtoJson.write(requestDto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("Нужны инструменты");
        assertThat(content).extractingJsonPathNumberValue("$.requestorId").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.created").isEqualTo("2024-01-01T10:00:00");

        assertThat(content).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Дрель");
        assertThat(content).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(2);
        assertThat(content).extractingJsonPathNumberValue("$.items[1].id").isEqualTo(2);
        assertThat(content).extractingJsonPathStringValue("$.items[1].name").isEqualTo("Молоток");
        assertThat(content).extractingJsonPathNumberValue("$.items[1].ownerId").isEqualTo(3);
    }

    @Test
    public void testItemRequestDtoDeserializationWithItems() throws IOException {
        String json = """
            {
                "id": 1,
                "description": "Нужна дрель",
                "requestorId": 1,
                "created": "2024-01-01T10:00:00",
                "items": [
                    {
                        "id": 1,
                        "name": "Дрель",
                        "ownerId": 2
                    },
                    {
                        "id": 2,
                        "name": "Молоток",
                        "ownerId": 3
                    }
                ]
            }
            """;

        ItemRequestDto requestDto = itemRequestDtoJson.parseObject(json);

        assertThat(requestDto).isNotNull();
        assertThat(requestDto.getId()).isEqualTo(1L);
        assertThat(requestDto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(requestDto.getRequestorId()).isEqualTo(1L);
        assertThat(requestDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        assertThat(requestDto.getItems()).hasSize(2);
        assertThat(requestDto.getItems().get(0).getName()).isEqualTo("Дрель");
        assertThat(requestDto.getItems().get(1).getName()).isEqualTo("Молоток");
    }
}