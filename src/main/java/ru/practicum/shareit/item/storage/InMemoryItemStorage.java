package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Repository
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();

    private long getNextItemId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Item createItem(Item item) {
        item.setId(getNextItemId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteItem(Long itemId) {
        items.remove(itemId);
    }

    @Override
    public Item getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId))
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с идентификатором - %d не найден", itemId)));
    }

    @Override
    public Collection<Item> getItemByText(String text) {
        return items.values().stream()
                .filter(item -> checkItem(item, text)).toList();
    }

    @Override
    public Collection<Item> getAllItems() {
        return items.values();
    }

    private boolean checkItem(Item item, String text) {
        return item.getAvailable().equals(Boolean.TRUE)
                && (item.getName().toLowerCase().contains(text) || item.getName().equalsIgnoreCase(text)
                || item.getDescription().toLowerCase().contains(text));
    }
}
