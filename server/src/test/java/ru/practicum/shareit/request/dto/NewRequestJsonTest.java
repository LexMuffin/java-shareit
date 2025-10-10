package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class NewRequestJsonTest {

    @Autowired
    private JacksonTester<NewRequest> newRequestJson;

    @Test
    public void testNewRequestSerialization() throws IOException {
        NewRequest newRequest = new NewRequest("Нужна дрель для ремонта", 1L);

        JsonContent<NewRequest> content = newRequestJson.write(newRequest);

        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель для ремонта");
        assertThat(content).extractingJsonPathNumberValue("$.requestorId").isEqualTo(1);
    }

    @Test
    public void testNewRequestDeserialization() throws IOException {
        String json = """
            {
                "description": "Нужна дрель для ремонта",
                "requestorId": 1
            }
            """;

        NewRequest newRequest = newRequestJson.parseObject(json);

        assertThat(newRequest).isNotNull();
        assertThat(newRequest.getDescription()).isEqualTo("Нужна дрель для ремонта");
        assertThat(newRequest.getRequestorId()).isEqualTo(1L);
    }

    @Test
    public void testNewRequestDeserializationWithOnlyDescription() throws IOException {
        String json = """
            {
                "description": "Нужен молоток"
            }
            """;

        NewRequest newRequest = newRequestJson.parseObject(json);

        assertThat(newRequest).isNotNull();
        assertThat(newRequest.getDescription()).isEqualTo("Нужен молоток");
        assertThat(newRequest.getRequestorId()).isNull();
    }

    @Test
    public void testNewRequestDeserializationWithOnlyRequestorId() throws IOException {
        String json = """
            {
                "requestorId": 5
            }
            """;

        NewRequest newRequest = newRequestJson.parseObject(json);

        assertThat(newRequest).isNotNull();
        assertThat(newRequest.getDescription()).isNull();
        assertThat(newRequest.getRequestorId()).isEqualTo(5L);
    }

    @Test
    public void testNewRequestDeserializationWithEmptyObject() throws IOException {
        String json = "{}";

        NewRequest newRequest = newRequestJson.parseObject(json);

        assertThat(newRequest).isNotNull();
        assertThat(newRequest.getDescription()).isNull();
        assertThat(newRequest.getRequestorId()).isNull();
    }

    @Test
    public void testNewRequestDeserializationWithEmptyDescription() throws IOException {
        String json = """
            {
                "description": "",
                "requestorId": 1
            }
            """;

        NewRequest newRequest = newRequestJson.parseObject(json);

        assertThat(newRequest).isNotNull();
        assertThat(newRequest.getDescription()).isEmpty();
        assertThat(newRequest.getRequestorId()).isEqualTo(1L);
    }

    @Test
    public void testNewRequestDeserializationWithWhitespaceDescription() throws IOException {
        String json = """
            {
                "description": "   ",
                "requestorId": 2
            }
            """;

        NewRequest newRequest = newRequestJson.parseObject(json);

        assertThat(newRequest).isNotNull();
        assertThat(newRequest.getDescription()).isEqualTo("   ");
        assertThat(newRequest.getRequestorId()).isEqualTo(2L);
    }
}