package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ResponseDtoJsonTest {

    @Autowired
    private JacksonTester<ResponseDto> responseDtoJson;

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
        String json = """
            {
                "id": 1,
                "name": "name",
                "ownerId": 2
            }
            """;

        ResponseDto responseDto = responseDtoJson.parseObject(json);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(1L);
        assertThat(responseDto.getName()).isEqualTo("name");
        assertThat(responseDto.getOwnerId()).isEqualTo(2L);
    }

    @Test
    public void testResponseDtoSerializationWithNullValues() throws IOException {
        ResponseDto responseDto = new ResponseDto(null, null, null);

        JsonContent<ResponseDto> content = responseDtoJson.write(responseDto);

        assertThat(content).extractingJsonPathStringValue("$.id").isNull();
        assertThat(content).extractingJsonPathStringValue("$.name").isNull();
        assertThat(content).extractingJsonPathStringValue("$.ownerId").isNull();
    }

    @Test
    public void testResponseDtoDeserializationWithPartialData() throws IOException {
        String json = """
            {
                "name": "Дрель"
            }
            """;

        ResponseDto responseDto = responseDtoJson.parseObject(json);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isNull();
        assertThat(responseDto.getName()).isEqualTo("Дрель");
        assertThat(responseDto.getOwnerId()).isNull();
    }
}