package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build()
        );
    }

    public ResponseEntity<Object> createItem(Long ownerId, NewItemRequest request) {
        return post("", ownerId, request);
    }

    public ResponseEntity<Object> updateItem(Long itemId, UpdateItemRequest request, Long ownerId) {
        return patch("/" + itemId, ownerId, request);
    }

    public ResponseEntity<Object> deleteItem(Long itemId) {
        return delete("/" + itemId);
    }

    public ResponseEntity<Object> getItemById(Long itemId, Long ownerId) {
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> getItemByText(Long ownerId, String text) {
        Map<String, Object> parameters = Map.of("text", (text == null ? "" : text));
        return get("/search?text={text}", ownerId, Map.of("text", ""));

    }

    public ResponseEntity<Object> getAllItemsById(Long ownerId) {
        return get("/" + ownerId);
    }

    public ResponseEntity<Object> addComment(Long authorId, Long itemId, NewCommentRequest newCommentRequest) {
        return post("/" + itemId + "/comment" , authorId, newCommentRequest);
    }


}
