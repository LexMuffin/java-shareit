package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.dto.UpdateRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class RequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> itemRequestDtoJson;

    @Autowired
    private JacksonTester<ResponseDto> responseDtoJson;

    @Autowired
    private JacksonTester<UpdateRequest> updateRequestJson;

    @Autowired
    private JacksonTester<NewRequest> newRequestJson;

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
        String json = "{" +
                "\"id\": 1," +
                "\"description\": \"Нужна дрель\"," +
                "\"requestorId\": 1," +
                "\"created\": \"2024-01-01T10:00:00\"," +
                "\"items\": []" +
                "}";

        ItemRequestDto requestDto = itemRequestDtoJson.parseObject(json);

        assertThat(requestDto).isNotNull();
        assertThat(requestDto.getId()).isEqualTo(1);
        assertThat(requestDto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(requestDto.getRequestorId()).isEqualTo(1);
        assertThat(requestDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        assertThat(requestDto.getItems()).isEmpty();
    }

    @Test
    public void testResponseDtoSerialization() throws IOException {
        ResponseDto responseDto = new ResponseDto(1L, "name", 2L);

        JsonContent<ResponseDto> content = responseDtoJson.write(responseDto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(content).extractingJsonPathNumberValue("$.ownerId").isEqualTo(2);
    }

    @Test
    public void testResponseDtoDeserialization() throws IOException {
        String json = "{" +
                "\"id\": 1," +
                "\"name\": \"name\"," +
                "\"ownerId\": 2" +
                "}";

        ResponseDto responseDto = responseDtoJson.parseObject(json);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(1L);
        assertThat(responseDto.getName()).isEqualTo("name");
        assertThat(responseDto.getOwnerId()).isEqualTo(2L);
    }

    @Test
    public void testUpdateRequestSerialization() throws IOException {
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        UpdateRequest updateRequest = new UpdateRequest(1L, "Обновленное описание", 1L, created);

        JsonContent<UpdateRequest> content = updateRequestJson.write(updateRequest);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("Обновленное описание");
        assertThat(content).extractingJsonPathNumberValue("$.requestorId").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.created").isEqualTo("2024-01-01T10:00:00");
    }

    @Test
    public void testUpdateRequestDeserialization() throws IOException {
        String json = "{" +
                "\"id\": 1," +
                "\"description\": \"Обновленное описание\"," +
                "\"requestorId\": 1," +
                "\"created\": \"2024-01-01T10:00:00\"" +
                "}";

        UpdateRequest updateRequest = updateRequestJson.parseObject(json);

        assertThat(updateRequest).isNotNull();
        assertThat(updateRequest.getId()).isEqualTo(1L);
        assertThat(updateRequest.getDescription()).isEqualTo("Обновленное описание");
        assertThat(updateRequest.getRequestorId()).isEqualTo(1L);
        assertThat(updateRequest.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0, 0));
    }

    @Test
    public void testNewRequestSerialization() throws IOException {
        NewRequest newRequest = new NewRequest("Нужна дрель для ремонта", 1L);

        JsonContent<NewRequest> content = newRequestJson.write(newRequest);

        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель для ремонта");
        assertThat(content).extractingJsonPathNumberValue("$.requestorId").isEqualTo(1);
    }

    @Test
    public void testNewRequestDeserialization() throws IOException {
        String json = "{" +
                "\"description\": \"Нужна дрель для ремонта\"," +
                "\"requestorId\": 1" +
                "}";

        NewRequest newRequest = newRequestJson.parseObject(json);

        assertThat(newRequest).isNotNull();
        assertThat(newRequest.getDescription()).isEqualTo("Нужна дрель для ремонта");
        assertThat(newRequest.getRequestorId()).isEqualTo(1L);
    }
}
